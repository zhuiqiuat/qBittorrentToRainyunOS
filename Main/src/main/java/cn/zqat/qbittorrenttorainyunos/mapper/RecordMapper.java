package cn.zqat.qbittorrenttorainyunos.mapper;

import cn.zqat.qbittorrenttorainyunos.pojo.Torrent;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

import java.util.List;

@Mapper
public interface RecordMapper {

    @Insert("insert into record (name,size,update_time) values (#{name},#{size},#{updateTime})")
    void insert(Torrent torrent);


    @Select("select * from record order by update_time desc")
    List<Torrent> getAll();
}
