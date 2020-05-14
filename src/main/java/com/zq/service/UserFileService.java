package com.zq.service;

import com.zq.bean.UserFile;

import java.util.List;

public interface UserFileService {

    //根据userId查询对应的所有文件信息
    List<UserFile> getFilesByUserId(Integer id);

    //保存用户信息
    void SaveFile(UserFile userFile);

    //根据对应的Id查询出对应的文件
    UserFile getFilesById(Integer id);

    //更新
    void update(UserFile userFile);

    void delete(Integer id);
}
