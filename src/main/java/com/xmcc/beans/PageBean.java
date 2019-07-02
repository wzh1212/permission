package com.xmcc.beans;

import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.Min;
import java.util.ArrayList;
import java.util.List;

public class PageBean<T> {

    // 当前页码
    @Getter
    @Setter
    @Min(value = 0,message = "当前页码不合法")
    private int pageNo = 1;


    // 每页显示的条数
    @Getter
    @Setter
    @Min(value = 0)
    private int pageSize = 10;

    // 每次从哪一条开始查询
    @Min(value = 0)
    private int offset = 1;

    // 分页从哪里开始查询下一条数据，limit ？，？
    public int getOffset() {
        return (pageNo - 1) * pageSize;  // 第一个 ？
    }

    public void setOffset(int offset) {
        this.offset = offset;
    }

    @Getter
    @Setter
    private int total = 0;

    @Getter
    @Setter
    private List<T> data = new ArrayList<>();
}
