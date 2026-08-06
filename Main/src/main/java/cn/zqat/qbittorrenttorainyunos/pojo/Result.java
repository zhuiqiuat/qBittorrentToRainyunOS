package cn.zqat.qbittorrenttorainyunos.pojo;

import lombok.Data;

@Data
public class Result {

    private Integer code;
    private String msg;
    private Object data;

    public static Result suc(Object data){
        Result rs = new Result();
        rs.code = 1;
        rs.msg = "success";
        rs.data = data;
        return rs;
    }

    public static Result suc(){
        Result rs = new Result();
        rs.code = 1;
        rs.msg = "success";
        return rs;
    }

    public static Result err(String msg){
        Result rs = new Result();
        rs.code = 0;
        rs.msg = msg;
        return rs;
    }

}
