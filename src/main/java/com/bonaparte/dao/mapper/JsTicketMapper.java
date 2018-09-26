package com.bonaparte.dao.mapper;

import com.bonaparte.entity.JsTicket;
import com.bonaparte.util.MyMapper;

public interface JsTicketMapper extends MyMapper<JsTicket> {
    public JsTicket getValid(JsTicket query);
}