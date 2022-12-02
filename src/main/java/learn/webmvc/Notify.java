package learn.webmvc;

import java.util.Date;

import lombok.Data;

@Data
public class Notify {

    /** 노티 발생 시간 ms */
    long when;
    /** 노티 종류 */
    String type;
    /** 보낸이 */
    String from;
    /** 받는이 */
    String to;
    /** 메시지 */
    String message;

    /** ID. 자동생성 */
    int id;
    /** 받은 시간 */
    Date date = new Date();
    
}
