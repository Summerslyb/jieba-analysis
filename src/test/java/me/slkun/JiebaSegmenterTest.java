package me.slkun;

import me.slkun.JiebaSegmenter;
import me.slkun.JiebaSegmenter.SegMode;
import me.slkun.WordDictionary;
import me.slkun.bean.SegToken;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.nio.file.Paths;
import java.util.List;
import java.util.Locale;

public class JiebaSegmenterTest {
    private JiebaSegmenter segmenter = new JiebaSegmenter();
    private String[] sentences = new String[]{
            "一个被遗落的名字，一段被忘却的记忆，",
            "迷失于妄想的少女，窥伺其灵魂的恶鬼。",
            "在背弃之后寻求宽恕，",
            "为她献上爱与恨的忏魂曲。",
            "追求音乐梦想的少女——泠珞，",
            "意外遇见黑色的身影，",
            "身后追杀自己的不死恶魔，",
            "梦中守护自己的完美恋人……",
            "而现实的异变，",
            "终于也让泠珞回忆起了不愿面对的过去……",
            "背后的真相到底是什么？",
            "破碎的梦想又何去何从？",
            "游戏《妄想症：Deliver Me》是一款音乐主题的青春校园AVG游戏，故事内容根据雨狸的《妄想症Paranoia》小说改编。玩家将扮演高中女生泠珞，体验青春、音乐与校园的缤纷故事。"
    };
    private String[] longSentences = new String[]{
            "《妄想症：Deliver Me》是属音制作组制作的一款音乐主题的青春校园AVG游戏。故事内容根据雨狸的《妄想症Paranoia》小说改编，讲述了一个追逐音乐梦想的少女泠珞因为失去伙伴，失去对他人和世界的信任，被自己一层一层的妄想困住的故事。玩家将扮演泠珞，体验青春、音乐与校园的缤纷故事，同时收集不同的回忆线索，找回失落的乐谱，解开层层谜题，回忆起令人震惊的真相，最终尝试走出妄想世界并重获新生。",
    };


    private void printOnlyWord(List<SegToken> tokens) {
        StringBuilder builder = new StringBuilder();
        for(SegToken token : tokens){
            builder.append(token.getWord()).append(" ");
        }
        System.out.println(builder.toString());
    }


    @BeforeAll
    public static void init() {
        WordDictionary.getInstance().init(Paths.get("conf"));
    }


    @Test
    public void testCutForSearch() {
        System.out.println("SEARCH: ");
        for(String sentence : sentences){
            List<SegToken> tokens = segmenter.process(sentence, SegMode.SEARCH);
            printOnlyWord(tokens);
        }
        for(String sentence : longSentences){
            List<SegToken> tokens = segmenter.process(sentence, SegMode.SEARCH);
            printOnlyWord(tokens);
        }
    }


    @Test
    public void testCutForIndex() {
        System.out.println("INDEX: ");
        for(String sentence : sentences){
            List<SegToken> tokens = segmenter.process(sentence, SegMode.INDEX);
            printOnlyWord(tokens);
        }
        for(String sentence : longSentences){
            List<SegToken> tokens = segmenter.process(sentence, SegMode.INDEX);
            printOnlyWord(tokens);
        }
    }


    @Test
    public void testSegmentSpeed() {
        long length = 0L;
        long wordCount = 0L;
        long start = System.currentTimeMillis();
        for(int i = 0; i < 2000; ++i)
            for(String sentence : sentences){
                segmenter.process(sentence, SegMode.INDEX);
                length += sentence.getBytes(StandardCharsets.UTF_8).length;
                wordCount += sentence.length();
            }
        long elapsed = (System.currentTimeMillis() - start);
        System.out.printf(Locale.CHINA, "testSegmentSpeed: time elapsed:%d, rate:%fkb/s, sentences:%.2f/s\n", elapsed,
                (length * 1.0) / 1024.0f / (elapsed * 1.0 / 1000.0f), wordCount * 1000.0f / (elapsed * 1.0));
    }


    @Test
    public void testLongTextSegmentSpeed() {
        long length = 0L;
        long wordCount = 0L;
        long start = System.currentTimeMillis();
        for(int i = 0; i < 100; ++i)
            for(String sentence : longSentences){
                segmenter.process(sentence, SegMode.INDEX);
                length += sentence.getBytes(StandardCharsets.UTF_8).length;
                wordCount += sentence.length();
            }
        long elapsed = (System.currentTimeMillis() - start);
        System.out.printf(Locale.CHINA, "testLongTextSegmentSpeed: time elapsed:%d, rate:%fkb/s, sentences:%.2f/s\n", elapsed,
                (length * 1.0) / 1024.0f / (elapsed * 1.0 / 1000.0f), wordCount * 1000.0f / (elapsed * 1.0));
    }

    @Test
    public void testReadFromFile() throws IOException {
        InputStream is = this.getClass().getResourceAsStream("/test.txt");
        BufferedReader br = new BufferedReader(new InputStreamReader(is, Charset.forName("UTF-8")));
        while(br.ready()){
            String line = br.readLine();

            List<SegToken> tokens = segmenter.process(line, SegMode.SEARCH);
            printOnlyWord(tokens);
        }
    }
}
