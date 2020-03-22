package com.example.zsbenweather.gson;

import java.util.List;

public class NewsJson {

    /**
     * date : 20200322
     * stories : [{"image_hue":"0x9f7b6a","title":"如何评价电视剧《安家》中的张乘乘？","url":"https://daily.zhihu.com/story/9721896","hint":"张萌 · 1 分钟阅读","ga_prefix":"032211","images":["https://pic2.zhimg.com/v2-b22d8233504539b1c2482ad103671809.jpg"],"type":0,"id":9721896},{"image_hue":"0x946d68","title":"为什么人会困？","url":"https://daily.zhihu.com/story/9721891","hint":"BioArt生物艺术 · 6 分钟阅读","ga_prefix":"032209","images":["https://pic4.zhimg.com/v2-a0c01f76888cf6ad1a80e07dcab6975f.jpg"],"type":0,"id":9721891},{"image_hue":"0xb09c7b","title":"有哪些常被认错的动物？","url":"https://daily.zhihu.com/story/9721881","hint":"溯溪觅云踪 · 8 分钟阅读","ga_prefix":"032207","images":["https://pic2.zhimg.com/v2-dfdf8ae5a482dacf2abdf9d488b8204d.jpg"],"type":0,"id":9721881}]
     * top_stories : [{"image_hue":"0xb3947d","hint":"作者 / 牛顿顿顿","url":"https://daily.zhihu.com/story/9721278","image":"https://pic2.zhimg.com/v2-cf632722ef85178785609849e8e2bf61.jpg","title":"如何看待网上五花八门的「寻人服务」？","ga_prefix":"030811","type":0,"id":9721278},{"image_hue":"0x3b4954","hint":"作者 / 老伏","url":"https://daily.zhihu.com/story/9721187","image":"https://pic2.zhimg.com/v2-1bc6922cc3da00da1326fd15c7b9fc7d.jpg","title":"假如你洗澡时，元素周期表里的元素挨个飞来会怎样？（一）","ga_prefix":"030507","type":0,"id":9721187},{"image_hue":"0x8f8864","hint":"作者 / 一丁","url":"https://daily.zhihu.com/story/9721127","image":"https://pic4.zhimg.com/v2-b35cc03ed77a6f3bd4892722556b11e7.jpg","title":"如何看待同人作品的法律风险？","ga_prefix":"030409","type":0,"id":9721127},{"image_hue":"0xb3947d","hint":"作者 / 我是一只小萌刀","url":"https://daily.zhihu.com/story/9720746","image":"https://pic3.zhimg.com/v2-a2e68715865ee4717110c684ff34072e.jpg","title":"历史上有哪些英雄人物在晚年对自己一生的评价？","ga_prefix":"022411","type":0,"id":9720746},{"image_hue":"0x051e27","hint":"作者 / 羽则","url":"https://daily.zhihu.com/story/9720668","image":"https://pic2.zhimg.com/v2-162fbbfbf55aba80dc8a50c7f989d67d.jpg","title":"你见过最野的黑客什么样？","ga_prefix":"022209","type":0,"id":9720668}]
     */

    private String date;
    private List<StoriesBean> stories;
    private List<TopStoriesBean> top_stories;

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public List<StoriesBean> getStories() {
        return stories;
    }

    public void setStories(List<StoriesBean> stories) {
        this.stories = stories;
    }

    public List<TopStoriesBean> getTop_stories() {
        return top_stories;
    }

    public void setTop_stories(List<TopStoriesBean> top_stories) {
        this.top_stories = top_stories;
    }

    public static class StoriesBean {
        /**
         * image_hue : 0x9f7b6a
         * title : 如何评价电视剧《安家》中的张乘乘？
         * url : https://daily.zhihu.com/story/9721896
         * hint : 张萌 · 1 分钟阅读
         * ga_prefix : 032211
         * images : ["https://pic2.zhimg.com/v2-b22d8233504539b1c2482ad103671809.jpg"]
         * type : 0
         * id : 9721896
         */

        private String image_hue;
        private String title;
        private String url;
        private String hint;
        private String ga_prefix;
        private int type;
        private int id;
        private List<String> images;

        public String getImage_hue() {
            return image_hue;
        }

        public void setImage_hue(String image_hue) {
            this.image_hue = image_hue;
        }

        public String getTitle() {
            return title;
        }

        public void setTitle(String title) {
            this.title = title;
        }

        public String getUrl() {
            return url;
        }

        public void setUrl(String url) {
            this.url = url;
        }

        public String getHint() {
            return hint;
        }

        public void setHint(String hint) {
            this.hint = hint;
        }

        public String getGa_prefix() {
            return ga_prefix;
        }

        public void setGa_prefix(String ga_prefix) {
            this.ga_prefix = ga_prefix;
        }

        public int getType() {
            return type;
        }

        public void setType(int type) {
            this.type = type;
        }

        public int getId() {
            return id;
        }

        public void setId(int id) {
            this.id = id;
        }

        public List<String> getImages() {
            return images;
        }

        public void setImages(List<String> images) {
            this.images = images;
        }
    }

    public static class TopStoriesBean {
        /**
         * image_hue : 0xb3947d
         * hint : 作者 / 牛顿顿顿
         * url : https://daily.zhihu.com/story/9721278
         * image : https://pic2.zhimg.com/v2-cf632722ef85178785609849e8e2bf61.jpg
         * title : 如何看待网上五花八门的「寻人服务」？
         * ga_prefix : 030811
         * type : 0
         * id : 9721278
         */

        private String image_hue;
        private String hint;
        private String url;
        private String image;
        private String title;
        private String ga_prefix;
        private int type;
        private int id;

        public String getImage_hue() {
            return image_hue;
        }

        public void setImage_hue(String image_hue) {
            this.image_hue = image_hue;
        }

        public String getHint() {
            return hint;
        }

        public void setHint(String hint) {
            this.hint = hint;
        }

        public String getUrl() {
            return url;
        }

        public void setUrl(String url) {
            this.url = url;
        }

        public String getImage() {
            return image;
        }

        public void setImage(String image) {
            this.image = image;
        }

        public String getTitle() {
            return title;
        }

        public void setTitle(String title) {
            this.title = title;
        }

        public String getGa_prefix() {
            return ga_prefix;
        }

        public void setGa_prefix(String ga_prefix) {
            this.ga_prefix = ga_prefix;
        }

        public int getType() {
            return type;
        }

        public void setType(int type) {
            this.type = type;
        }

        public int getId() {
            return id;
        }

        public void setId(int id) {
            this.id = id;
        }
    }
}
