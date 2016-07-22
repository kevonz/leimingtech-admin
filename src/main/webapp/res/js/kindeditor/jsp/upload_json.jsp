<%@ page language="java" contentType="text/html; charset=UTF-8"
         pageEncoding="UTF-8"%>
<%@ page import="java.util.*,java.io.*"%>
<%@ page import="java.text.SimpleDateFormat"%>
<%@ page import="org.apache.commons.fileupload.*"%>
<%@ page import="org.apache.commons.fileupload.disk.*"%>
<%@ page import="org.apache.commons.fileupload.servlet.*"%>
<%@ page import="com.leimingtech.core.jackson.JsonUtils"%>
<%@ page import="org.springframework.web.multipart.support.*"%>
<%@ page import="org.springframework.web.multipart.*"%>
<%@ page import="org.springframework.web.multipart.commons.*"%>
<%@ page import="com.google.common.collect.Maps" %>

<%

    /**
     * KindEditor JSP
     *
     * 本JSP程序是演示程序，建议不要直接在实际项目中使用。
     * 如果您确定直接使用本程序，使用之前请仔细确认相关安全设置。
     *
     */

//文件保存目录路径
    String savePath = pageContext.getServletContext().getRealPath("/") + "attached/";

//文件保存目录URL
    String saveUrl  = request.getContextPath() + "/attached/";

//定义允许上传的文件扩展名
    HashMap<String, String> extMap = new HashMap<String, String>();
    extMap.put("image", "gif,jpg,jpeg,png,bmp");
    extMap.put("flash", "swf,flv");
    extMap.put("media", "swf,flv,mp3,wav,wma,wmv,mid,avi,mpg,asf,rm,rmvb");
    extMap.put("file", "pdf,doc,docx,xls,xlsx,ppt,htm,html,txt,zip,rar,gz,bz2,java,php,exe,gif,jpg,jpeg,png,bmp");

//最大文件大小 100M
    long maxSize = 102400000;

    response.setContentType("text/html; charset=UTF-8");

    if(!ServletFileUpload.isMultipartContent(request)){
        out.println(getError("请选择文件。"));
        return;
    }
//检查目录
    File uploadDir = new File(savePath);
    if(!uploadDir.isDirectory()){
        out.println(getError("上传目录不存在。"));
        return;
    }
//检查目录写权限
    if(!uploadDir.canWrite()){
        out.println(getError("上传目录没有写权限。"));
        return;
    }

    String dirName = request.getParameter("dir");
    if (dirName == null) {
        dirName = "image";
    }
    if(!extMap.containsKey(dirName)){
        out.println(getError("目录名不正确。"));
        return;
    }
//创建文件夹
    savePath += dirName + "/";
    saveUrl += dirName + "/";
    File saveDirFile = new File(savePath);
    if (!saveDirFile.exists()) {
        saveDirFile.mkdirs();
    }
    SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd");
    String ymd = sdf.format(new Date());
    savePath += ymd + "/";
    saveUrl += ymd + "/";
    File dirFile = new File(savePath);
    if (!dirFile.exists()) {
        dirFile.mkdirs();
    }

    FileItemFactory factory = new DiskFileItemFactory();
    ServletFileUpload upload = new ServletFileUpload(factory);
    upload.setHeaderEncoding("UTF-8");
    List items = upload.parseRequest(request);

    DefaultMultipartHttpServletRequest mrequest= (DefaultMultipartHttpServletRequest)request;

    Map map=mrequest.getFileMap();
    Collection<MultipartFile> c = map.values();
    Iterator it = c.iterator();
    for (; it.hasNext();) {
        CommonsMultipartFile file=(CommonsMultipartFile) it.next();

        if(!file.isEmpty())
        {
            long fileSize = file.getSize();
            String fileName = file.getOriginalFilename();
            String contentType=file.getContentType();
            String fileExt = fileName.substring(fileName.lastIndexOf(".") + 1).toLowerCase();

            if(fileSize > maxSize){
                out.println(getError("上传文件大小超过限制100M。"));
                return;
            }
            if(!Arrays.<String>asList(extMap.get(dirName).split(",")).contains(fileExt)){
                out.println(getError("上传文件扩展名是不允许的扩展名。\n只允许" + extMap.get(dirName) + "格式。"));
                return;
            }

            FileItem item = null;
            SimpleDateFormat df = new SimpleDateFormat("yyyyMMddHHmmss");
            String newFileName = df.format(new Date()) + "_" + new Random().nextInt(1000) + "." + fileExt;

            try{
                File uploadedFile = new File(savePath, newFileName);
                file.transferTo(uploadedFile);
            }catch(Exception e){

                out.println(getError("上传文件失败。"));
                return;
            }

            Map<String,Object> maps = Maps.newHashMap();
            maps.put("error", 0);
            maps.put("url", saveUrl + newFileName);
            maps.put("title", fileName);
            out.println(JsonUtils.toJsonStr(maps));

        }
        else
        {
            out.println(getError("不可以上传空文件！"));
            return;
        }

    }
%>
<%!
    private String getError(String message) {
        Map<String,Object> maps = Maps.newHashMap();
        maps.put("error", 1);
        maps.put("message", message);
        return JsonUtils.toJsonStr(maps);
    }
%>