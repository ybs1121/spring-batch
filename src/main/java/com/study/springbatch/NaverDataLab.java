package com.study.springbatch;

import lombok.Data;

import java.util.List;

@Data
public class NaverDataLab {

    public class Request {
        private String startDate;
        private String endDate;
        private String timeUnit;
        private List<KeywordGroup> keywordGroups;
        private String device;
        private List<String> ages;
        private String gender;

        public static class KeywordGroup {
            private String groupName;
            private List<String> keywords;
        }
    }
}