package com.zq.controller;

import com.zq.bean.User;
import com.zq.bean.UserFile;
import com.zq.service.UserFileService;
import org.apache.commons.io.FilenameUtils;
import org.apache.commons.io.IOUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.ResourceUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.*;
import java.net.URLEncoder;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.UUID;

@Controller
@RequestMapping("/file")
public class UserFileController {

    @Autowired
    private UserFileService userFileService;

    @GetMapping("/showAll")
    public String findAll(HttpSession session, Model model){
        //在登入的session中获取Id
        User user = (User) session.getAttribute("user");
        List<UserFile> files = userFileService.getFilesByUserId(user.getId());
        model.addAttribute("files",files);
        return "showAll";
    }


    //上传文件的处理 并保持文件信息保存到数据库
    @PostMapping("/upload")
    public String upload(MultipartFile aaa, HttpSession session, RedirectAttributes attributes) throws IOException {

        if (aaa.getContentType().equals("application/octet-stream")){
            System.out.println(1);
            attributes.addFlashAttribute("errorMessage","不能上传空文件！");
        }else {
            //获取当前登入的对象
            User user = (User) session.getAttribute("user");

            //获取文件的原始名称
            String oldFileName = aaa.getOriginalFilename();

            //获取文件的后缀
            String extension = "." + FilenameUtils.getExtension(aaa.getOriginalFilename());

            //生成新的文件名称
            String newFileName = new SimpleDateFormat("yyyyMMddHHmmss").format(new Date()) + UUID.randomUUID().toString().replace("-","").substring(6) + extension;

            //获取文件大小
            long size = aaa.getSize();

            //文件类型
            String type = aaa.getContentType();

            //处理根据日期生成目录
            String realPath = ResourceUtils.getURL("classpath:").getPath() + "/static/files";
            String format = new SimpleDateFormat("yyyy-MM-dd").format(new Date());
            String dateDirPath = realPath + "/" + format;
            File dataDir = new File(dateDirPath);
            if (!dataDir.exists()){
                dataDir.mkdirs();
            }

            //处理文件上传
            aaa.transferTo(new File(dataDir,newFileName));

            UserFile userFile = new UserFile();
            userFile.setOldFileName(oldFileName);
            userFile.setNewFileName(newFileName);
            userFile.setExt(extension);
            userFile.setSize(String.valueOf(size));
            userFile.setType(type);
            userFile.setPath("/files/"+format);
            userFile.setUserId(user.getId());

            //将文件存入数据库
            userFileService.SaveFile(userFile);
        }

        return "redirect:/file/showAll";
    }

    //文件下载
    @GetMapping("/download")
    public void download(Integer id,String openStyle,HttpServletResponse response) throws IOException {
        //获取文件信息
        UserFile userfile = userFileService.getFilesById(id);
        //判断用户是在线打开还是下载
        openStyle = openStyle == null ? "attachment" : openStyle;
        if ("attachment".equals(openStyle)){
            //更新文件下载次数
            userfile.setDownCounts(userfile.getDownCounts()+1);
            userFileService.update(userfile);
        }
        //根据文件信息中文件的名字和文件存储的路径获取文件输入流
        String realpath = ResourceUtils.getURL("classpath:").getPath() + "/static" + userfile.getPath();
        //获取文件输入流
        FileInputStream is = new FileInputStream(new File(realpath, userfile.getNewFileName()));
        //附件下载
        response.setHeader("content-disposition",openStyle+";fileName="+ URLEncoder.encode(userfile.getOldFileName(),"UTF-8"));
        //获取响应输出流
        ServletOutputStream os = response.getOutputStream();
        //文件拷贝
        IOUtils.copy(is,os);
        IOUtils.closeQuietly(is);
        IOUtils.closeQuietly(os);
    }

    //删除文件信息
    @GetMapping("/delete")
    public String delete(Integer id) throws FileNotFoundException {
        //根据Id查询信息
        UserFile userFile = userFileService.getFilesById(id);

        //删除文件
        String realpath = ResourceUtils.getURL("classpath:").getPath() + "/static" + userFile.getPath();
        File file = new File(realpath, userFile.getNewFileName());
        if (file.exists()){
            file.delete();//立即删除
        }

        //删除数据库中的纪律
        userFileService.delete(id);
        return "redirect:/file/showAll";
    }
}
