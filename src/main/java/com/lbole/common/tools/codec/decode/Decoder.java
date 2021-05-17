package com.lbole.common.tools.codec.decode;

import com.lbole.common.tools.codec.offset.Offset;

/**
 * @Author 马嘉祺
 * @Date 2020/6/2 0002 14 55
 * @Description <p></p>
 */
public interface Decoder<T extends DObject> {
    
    String title();
    
    int byteLen();
    
    T decode(byte[] bytes, Offset offset);
    
    T decode(Object value);
    
    
    
}
