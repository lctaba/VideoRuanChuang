package com.zhaoss.weixinrecordeddemo.projectUtil;

import android.util.Xml;

import com.projectUtil.ErrorType;
import com.projectUtil.ErrorVideo;
import com.projectUtil.Project;
import com.projectUtil.Subtitle;
import com.projectUtil.VideoClip;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;
import org.xmlpull.v1.XmlSerializer;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.prefs.NodeChangeEvent;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

/**
 * @Author cyh
 * @Date 2021/3/20 13:32
 */
public class ProjectUtil {
    //public static String PROJECT_PATH = "/sdcard/WeiXinRecorded/";

    /**
     * 新建项目，新建项目后会自动创建文件夹并初始化一个项目文件
     * @param path app存储地址
     * @param name 项目名，path+/+name组成项目文件夹
     * @return false表面项目创建失败，有同名项目，true表示创建成功
     */
    public static boolean newProject(String path,String name){
        //PROJECT_PATH = path;
        File dir = new File(path+"/"+name);
        if(!dir.exists()){
            dir.mkdir();
        }
        //项目文件名
        File projectFile = new File(dir.getPath()+"/"+"project.xml");
        if(projectFile.exists()){
            return false;
        }
        newProjectFile(name, projectFile);
        return true;
    }

    private static void newProjectFile(String name, File projectFile) {
        try {
            FileOutputStream fos = new FileOutputStream(projectFile);
            //xml序列化工具
            XmlSerializer serializer = Xml.newSerializer();
            serializer.setOutput(fos,"utf-8");
            serializer.startDocument("utf-8",true);
            serializer.startTag(null,"project");
            serializer.attribute(null,"name",name);

            serializer.startTag(null,"materials");
            serializer.endTag(null,"materials");

            serializer.startTag(null,"videoClips");
            serializer.endTag(null,"videoClips");

            serializer.endTag(null,"project");
            serializer.endDocument();
            fos.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * 保存项目
     * @param path
     * @param project
     */
    public static void storeProject(String path,Project project){
        File dir = new File(path+"/"+project.name);
        if(!dir.exists()){
            dir.mkdir();
        }
        try {
            //项目文件名
            File projectFile = new File(dir.getPath()+"/"+"project.xml");
            //文件如存在则删除
            if (projectFile.exists()){
                projectFile.delete();
            }
            FileOutputStream fos = new FileOutputStream(projectFile);
            //xml序列化工具
            XmlSerializer serializer = Xml.newSerializer();
            serializer.setOutput(fos,"utf-8");
            serializer.startDocument("utf-8",true);
            serializer.startTag(null,"project");
            serializer.attribute(null,"name",project.name);

            //保存所有素材
            serializer.startTag(null,"materials");
            for(String video:project.videos){
                serializer.startTag(null,"material");
                serializer.text(video);
                serializer.endTag(null,"material");
            }
            serializer.endTag(null,"materials");


            //保存所有视频片段
            serializer.startTag(null,"videoClips");
            for(VideoClip videoClip:project.videoClips){
                serializer.startTag(null,"videoClip");
                //所属素材,以及在素材中的开始结束时间
                serializer.startTag(null,"belongTo");

                serializer.startTag(null,"name");
                serializer.text(videoClip.belongTo);
                serializer.endTag(null,"name");

                serializer.startTag(null,"start");
                serializer.text(videoClip.relativeStartTime.toString());
                serializer.endTag(null,"start");

                serializer.startTag(null,"end");
                serializer.text(videoClip.relativeEndTime.toString());
                serializer.endTag(null,"end");

                serializer.endTag(null,"belongTo");
                //在项目中的开始结束时间
                serializer.startTag(null,"span");

                serializer.startTag(null,"start");
                serializer.text(videoClip.startTime.toString());
                serializer.endTag(null,"start");

                serializer.startTag(null,"end");
                serializer.text(videoClip.endTime.toString());
                serializer.endTag(null,"end");

                serializer.endTag(null,"span");
                //字幕列表
                serializer.startTag(null,"subtitles");
                for (Subtitle subtitle:videoClip.subtitles){
                    serializer.startTag(null,"subtitle");

                    serializer.startTag(null,"start");
                    serializer.text(subtitle.startTime.toString());
                    serializer.endTag(null,"start");

                    serializer.startTag(null,"end");
                    serializer.text(subtitle.endTime.toString());
                    serializer.startTag(null,"end");

                    serializer.startTag(null,"text");
                    serializer.text(subtitle.subtitle);
                    serializer.endTag(null,"text");

                    serializer.endTag(null,"subtitle");
                }
                serializer.endTag(null,"subtitles");
                //错误片段列表
                serializer.startTag(null,"errorVideos");
                for(ErrorVideo errorVideo:videoClip.errorVideos){
                    serializer.startTag(null,"errorVideo");

                    serializer.startTag(null,"start");
                    serializer.text(errorVideo.startTime.toString());
                    serializer.endTag(null,"start");

                    serializer.startTag(null,"end");
                    serializer.text(errorVideo.endTime.toString());
                    serializer.endTag(null,"end");

                    serializer.startTag(null,"errorType");
                    serializer.text(errorVideo.errorType.toString());
                    serializer.endTag(null,"errorType");

                    serializer.endTag(null,"errorVideo");
                }
                serializer.endTag(null,"errorVideos");

                serializer.endTag(null,"videoClip");
            }
            serializer.endTag(null,"videoClips");

            serializer.endTag(null,"project");
            serializer.endDocument();
            fos.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * 解析项目文件
     * @param path 路径
     * @param name 项目名
     * @return
     */
    public static Project parse(String path, String name){
        File file = new File(path+"/"+name+"/project.xml");
        Project project = new Project();

        try {
            InputStream inputStream = new FileInputStream(file);
            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
            DocumentBuilder builder = factory.newDocumentBuilder();
            Document document = builder.parse(inputStream);
            Element root = document.getDocumentElement();
            //解析项目名
            project.name = root.getAttribute("name");
            NodeList properties = root.getChildNodes();
            //解析素材名
            for(int i=0; i<properties.getLength(); i++){
                Node property = properties.item(i);
                String propertyName = properties.item(i).getNodeName();
                switch (propertyName){
                    case "materials":
                        setMaterials(project, property);
                        break;
                    case "videoClips":
                        setVideoClips(property,project);
                        break;
                    default:
                        break;
                }
            }
        } catch (ParserConfigurationException | IOException | SAXException e) {
            e.printStackTrace();
        }

        return project;
    }

    /**
     * 解析视频片段
     * @param property
     * @param project
     */
    private static void setVideoClips(Node property,Project project) {
        VideoClip vc = new VideoClip();
        NodeList videoClips = property.getChildNodes();
        for(int j=0; j<videoClips.getLength(); j++){
            Node videoClip = videoClips.item(j);
            NodeList properties = videoClip.getChildNodes();
            for(int k=0; k<properties.getLength(); k++){
                Node node = properties.item(k);
                String nodeName = node.getNodeName();
                switch (nodeName){
                    case "belongTo":
                        setVideoClipBelongTo(vc, node);
                        break;
                    case "span":
                        setVideoClipSpan(vc, node);
                        break;
                    case "subtitles":
                        setVideoClipSubtitles(vc, node);
                        break;
                    case "errorVideos":
                        setVideoClipsErrorVideos(vc, node);
                        break;
                    default:
                        break;
                }
            }
        }
    }

    /**
     * 设置视频片段中的问题片段
     * @param vc
     * @param node
     */
    private static void setVideoClipsErrorVideos(VideoClip vc, Node node) {
        NodeList errorVideos = node.getChildNodes();
        for (int i=0; i<errorVideos.getLength(); i++){
            Node errorVideo = errorVideos.item(i);
            NodeList errorVideoProperties = errorVideo.getChildNodes();
            ErrorVideo e = new ErrorVideo();
            for (int n=0; n<errorVideoProperties.getLength(); n++){
                Node errorVideoProperty = errorVideoProperties.item(n);
                String errorVideoPropertyName = errorVideoProperty.getNodeName();
                switch (errorVideoPropertyName){
                    case "start":
                        e.startTime = Integer.parseInt(errorVideoProperty.getNodeValue());
                        break;
                    case "end":
                        e.endTime = Integer.parseInt(errorVideoProperty.getNodeValue());
                        break;
                    case "errorType":
                        e.errorType = ErrorType.valueOf(errorVideoProperty.getNodeValue());
                        break;
                    default:
                        break;
                }
            }
            vc.errorVideos.add(e);
        }
    }

    /**
     * 给视频片段设置字幕
     * @param vc
     * @param node
     */
    private static void setVideoClipSubtitles(VideoClip vc, Node node) {
        NodeList subtitles = node.getChildNodes();
        for (int i=0; i<subtitles.getLength(); i++){
            Node subtitle = subtitles.item(i);
            NodeList subtitleProperties = subtitle.getChildNodes();
            Subtitle s = new Subtitle();
            for(int n=0; n<subtitleProperties.getLength(); n++){
                Node subtitleProperty = subtitleProperties.item(n);
                String subtitlePropertyName = subtitleProperty.getNodeName();
                switch (subtitlePropertyName){
                    case "start":
                        s.startTime = Integer.parseInt(subtitleProperty.getNodeValue());
                        break;
                    case "end":
                        s.endTime = Integer.parseInt(subtitleProperty.getNodeValue());
                        break;
                    case "text":
                        s.subtitle = subtitleProperty.getNodeValue();
                        break;
                    default:
                        break;
                }
            }
            vc.subtitles.add(s);
        }
    }

    /**
     * 设置视频片段在项目中的时间
     * @param vc
     * @param node
     */
    private static void setVideoClipSpan(VideoClip vc, Node node) {
        NodeList span = node.getChildNodes();
        for (int i=0; i<span.getLength(); i++){
            Node spanProperty = span.item(i);
            String spanPropertyName = spanProperty.getNodeName();
            switch (spanPropertyName){
                case "start":
                    vc.startTime = Integer.parseInt(spanProperty.getNodeValue());
                    break;
                case "end":
                    vc.endTime = Integer.parseInt(spanProperty.getNodeValue());
                    break;
                default:
                    break;
            }
        }
    }

    /**
     * 设置视频片段来源的素材
     * @param vc
     * @param node
     */
    private static void setVideoClipBelongTo(VideoClip vc, Node node) {
        NodeList belongTo = node.getChildNodes();
        for (int i=0; i<belongTo.getLength(); i++){
            Node belongToProperty = belongTo.item(i);
            String belongToPropertyName = belongToProperty.getNodeName();
            switch (belongToPropertyName){
                case "name":
                    vc.belongTo = belongToProperty.getNodeValue();
                    break;
                case "start":
                    vc.relativeStartTime = Integer.parseInt(belongToProperty.getNodeValue());
                    break;
                case "end":
                    vc.relativeEndTime = Integer.parseInt(belongToProperty.getNodeValue());
                    break;
                default:
                    break;
            }
        }
    }

    /**
     * 解析素材
     * @param project
     * @param property
     */
    private static void setMaterials(Project project, Node property) {
        NodeList materials = property.getChildNodes();
        for (int j=0; j<materials.getLength(); j++){
            Node item = materials.item(j);
            project.videos.add(item.getNodeValue());
        }
    }
}
