package com.zq.mapper;

import com.zq.bean.UserFile;

import java.util.List;

public interface UserFileMapper {

    //根据Id查询该用户的所有文件
    List<UserFile> getFilesByUserId(Integer id);

    //保存文件信息
    void SaveFile(UserFile userFile);

    //根据Id查询对应文件
    UserFile getFilesById(Integer id);

    //更新
    void update(UserFile userFile);

    //根据Id删除记录
    void delete(Integer id);
}
