package io.nadron.util;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertSame;

import org.junit.Test;

import com.jelly.player.EmojiFilter;

public class EmojiFilterTest {


 /**
     * 测试emoji表情
     */
    @Test
    public void fileterEmoji() {
        String s = "😄1";
        String c = EmojiFilter.filterEmoji(s);
        System.out.println(s);
        System.out.println(c);
//        assertFalse(s.equals(c));
        String expected = "<body>213这是一个有各种内容的消息,  Hia Hia Hia !!!! xxxx@@@...*)" +
                "!(@*$&@(&#!)@*)!&$!)@^%@(!&#. ], ";
        assertEquals(expected, c);
//        assertSame(c, expected);
        assertSame(expected, "<body>213这是一个有各种内容的消息,  Hia Hia Hia !!!! xxxx@@@...*)" +
                "!(@*$&@(&#!)@*)!&$!)@^%@(!&#. ], ");
        assertSame(c, EmojiFilter.filterEmoji(c));
    }
}
