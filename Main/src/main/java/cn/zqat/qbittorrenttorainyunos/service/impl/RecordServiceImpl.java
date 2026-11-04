package cn.zqat.qbittorrenttorainyunos.service.impl;

import cn.zqat.qbittorrenttorainyunos.mapper.RecordMapper;
import cn.zqat.qbittorrenttorainyunos.pojo.Torrent;
import cn.zqat.qbittorrenttorainyunos.service.RecordService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class RecordServiceImpl implements RecordService {

    @Autowired
    private RecordMapper recordMapper;

    public void add(Torrent torrent) {
        recordMapper.insert(torrent);
    }

    public List<Torrent> getAll() {
        return recordMapper.getAll();
    }
}
