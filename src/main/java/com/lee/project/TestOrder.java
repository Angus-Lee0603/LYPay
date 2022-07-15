package com.lee.project;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.lee.pay.entity.BaseOrder;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.io.Serializable;
import java.math.BigDecimal;

@Data
@Builder
@EqualsAndHashCode(callSuper = false)
public class TestOrder extends BaseOrder implements Serializable {

    private static final long serialVersionUID = 1L;

    @TableId(value = "id", type = IdType.AUTO)
    public Integer id;

    public String orderId;

    public String userId;

    public BigDecimal realAmount;

    public String payMethod;

    public Integer state;
}
