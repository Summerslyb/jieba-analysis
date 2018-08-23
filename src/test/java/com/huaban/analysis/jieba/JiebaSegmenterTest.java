package com.huaban.analysis.jieba;

import com.huaban.analysis.jieba.JiebaSegmenter.SegMode;
import junit.framework.TestCase;
import org.junit.Test;

import java.nio.charset.StandardCharsets;
import java.nio.file.Paths;
import java.util.List;
import java.util.Locale;

/**
 * @author matrix
 */
public class JiebaSegmenterTest extends TestCase {
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


    @Override
    protected void setUp() throws Exception {
        super.setUp();
        WordDictionary.getInstance().init(Paths.get("conf"));
    }


    @Override
    protected void tearDown() throws Exception {
        super.tearDown();
    }


    @Test
    public void testCutForSearch() {
        for(String sentence : sentences){
            List<SegToken> tokens = segmenter.process(sentence, SegMode.SEARCH);
//            System.out.print(String.format(Locale.getDefault(), "\n%s\n%s", sentence, tokens.toString()));
            tokens.forEach(t -> System.out.print(t.word + " "));
            System.out.println();
        }
        for(String sentence : longSentences){
            List<SegToken> tokens = segmenter.process(sentence, SegMode.SEARCH);
//            System.out.print(String.format(Locale.getDefault(), "\n%s\n%s", sentence, tokens.toString()));
            tokens.forEach(t -> System.out.print(t.word + " "));
            System.out.println();
        }
    }


    @Test
    public void testCutForIndex() {
        for(String sentence : sentences){
            List<SegToken> tokens = segmenter.process(sentence, SegMode.INDEX);
            System.out.print(String.format(Locale.getDefault(), "\n%s\n%s", sentence, tokens.toString()));
        }
    }


    @Test
    public void testBugSentence() {
        String[] bugs =
                new String[]{
                        "UTF-8",
                        "iphone5",
                        "鲜芋仙 3",
                        "RT @laoshipukong : 27日，",
                        "AT&T是一件不错的公司，给你发offer了吗？",
                        "干脆就把那部蒙人的闲法给废了拉倒！RT @laoshipukong : 27日，全国人大常委会第三次审议侵权责任法草案，删除了有关医疗损害责任“举证倒置”的规定。在医患纠纷中本已处于弱势地位的消费者由此将陷入万劫不复的境地。 "};
        for(String sentence : bugs){
            List<SegToken> tokens = segmenter.process(sentence, SegMode.SEARCH);
            System.out.print(String.format(Locale.getDefault(), "\n%s\n%s", sentence, tokens.toString()));
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
        System.out.println(String.format(Locale.getDefault(), "time elapsed:%d, rate:%fkb/s, sentences:%.2f/s", elapsed,
                (length * 1.0) / 1024.0f / (elapsed * 1.0 / 1000.0f), wordCount * 1000.0f / (elapsed * 1.0)));
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
        System.out.println(String.format(Locale.getDefault(), "time elapsed:%d, rate:%fkb/s, sentences:%.2f/s", elapsed,
                (length * 1.0) / 1024.0f / (elapsed * 1.0 / 1000.0f), wordCount * 1000.0f / (elapsed * 1.0)));
    }

    public static void main(String[] args) throws Exception {
        JiebaSegmenterTest test = new JiebaSegmenterTest();
        test.setUp();
        test.testCutForSearch();
    }
}
