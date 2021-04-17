package android.helper.bean;

import com.android.helper.base.BaseEntity;

import java.util.List;

public class HomeBean extends BaseEntity {

    /**
     * returnCode : 1
     * returnStatus : 1
     * returnMsg : success
     * returnDataList : {"count":4,"data":[{"id":"ff808081776c490f01776d3d82aa0028","createTime":"2021-02-04 21:31:23","name":"西南大区活动","activityType":2,"activityNature":2,"agentId":"8ebf39cd6ee311e89d6c00163e03d68e","agentName":"北京三元桥","beginTime":"2021-02-04 21:31:00","endTime":"2021-02-20 21:30:00","showStatus":1,"lineType":1,"onlineTime":"2021-02-04 21:31:23","isLine":1,"href":"<p><img src=\"https://foxgoing.oss-cn-beijing.aliyuncs.com/img/ftms/20210204213041415-943.png\" style=\"\"><br><\/p>","img":"https://foxgoing.oss-cn-beijing.aliyuncs.com/img/ftms/20210204213027215-780.png","province":"110000","city":"110100","county":"110101","address":"2321312","maximumEnrolment":100,"maxinumCustomer":2,"applyBeginTime":"2021-02-04 21:31:00","applyEndTime":"2021-02-18 17:17:00","checkStatus":1,"checkOpinion":"123","contentType":1,"activityIntro":"西南大区活动"},{"id":"ff808081776c490f01776d1a698d0027","createTime":"2021-02-04 20:53:03","name":"20222","activityType":2,"activityNature":2,"agentId":"8ebf39cd6ee311e89d6c00163e03d68e","agentName":"北京三元桥","beginTime":"2021-02-04 20:53:00","endTime":"2021-02-28 18:18:00","showStatus":1,"lineType":1,"onlineTime":"2021-02-04 20:53:03","isLine":1,"href":"<p>202220222022202220222022<br><\/p>","img":"https://foxgoing.oss-cn-beijing.aliyuncs.com/img/ftms/20210204205223272-113.png","province":"110000","city":"110100","county":"110101","address":"2022","maximumEnrolment":100,"maxinumCustomer":2,"applyBeginTime":"2021-02-04 20:53:00","applyEndTime":"2021-02-26 14:14:00","checkStatus":1,"checkOpinion":"2022","contentType":1,"activityIntro":"2022"},{"id":"ff808081776c490f01776d15c6160026","createTime":"2021-02-04 20:47:59","name":"2022","activityType":2,"activityNature":2,"agentId":"8ebf39cd6ee311e89d6c00163e03d68e","agentName":"北京三元桥","beginTime":"2021-02-04 20:49:00","endTime":"2021-02-06 23:11:00","showStatus":1,"lineType":1,"onlineTime":"2021-02-04 20:47:59","isLine":1,"href":"https://www.baidu.com/","img":"https://foxgoing.oss-cn-beijing.aliyuncs.com/img/ftms/20210204204701434-249.png","province":"110000","city":"110100","county":"110105","address":"21312321312","maximumEnrolment":100,"maxinumCustomer":2,"applyBeginTime":"2021-02-04 20:48:00","applyEndTime":"2021-02-05 23:22:00","checkStatus":1,"checkOpinion":"2022","contentType":2,"activityIntro":"v2022"},{"id":"ff808081758792380175879de5760002","createTime":"2020-11-02 14:21:13","name":"测试云店分享1102","activityType":2,"activityNature":2,"agentId":"ff8080817181b982017181f0a1030003","agentName":"测试门店1","beginTime":"2020-11-02 14:21:00","endTime":"2021-03-31 21:21:00","showStatus":1,"lineType":1,"onlineTime":"2020-11-02 14:21:13","isLine":1,"href":"http://ftmsman.dijiahuche.com/lotteryActivity","img":"https://foxgoing.oss-cn-beijing.aliyuncs.com/img/ftms/20201102141958025-800.jpg","province":"120000","city":"120100","county":"120112","address":"朝阳区常营三间房","maximumEnrolment":10,"maxinumCustomer":10,"applyBeginTime":"2020-11-02 14:22:00","applyEndTime":"2020-11-30 21:21:00","checkStatus":1,"checkOpinion":"","contentType":2,"activityIntro":"测试云店分享1102","weChatProgramCodeUrl":"https://foxgoing.oss-cn-beijing.aliyuncs.com/efb500b8-e884-492a-a802-aa4721f4461b.png"}]}
     */

    private int returnCode;
    private int returnStatus;
    private String returnMsg;
    private ReturnDataList returnDataList;

    public int getReturnCode() {
        return returnCode;
    }

    public void setReturnCode(int returnCode) {
        this.returnCode = returnCode;
    }

    public int getReturnStatus() {
        return returnStatus;
    }

    public void setReturnStatus(int returnStatus) {
        this.returnStatus = returnStatus;
    }

    public String getReturnMsg() {
        return returnMsg;
    }

    public void setReturnMsg(String returnMsg) {
        this.returnMsg = returnMsg;
    }

    public ReturnDataList getReturnDataList() {
        return returnDataList;
    }

    public void setReturnDataList(ReturnDataList returnDataList) {
        this.returnDataList = returnDataList;
    }

    public static class ReturnDataList extends BaseEntity {
        /**
         * count : 4
         * data : [{"id":"ff808081776c490f01776d3d82aa0028","createTime":"2021-02-04 21:31:23","name":"西南大区活动","activityType":2,"activityNature":2,"agentId":"8ebf39cd6ee311e89d6c00163e03d68e","agentName":"北京三元桥","beginTime":"2021-02-04 21:31:00","endTime":"2021-02-20 21:30:00","showStatus":1,"lineType":1,"onlineTime":"2021-02-04 21:31:23","isLine":1,"href":"<p><img src=\"https://foxgoing.oss-cn-beijing.aliyuncs.com/img/ftms/20210204213041415-943.png\" style=\"\"><br><\/p>","img":"https://foxgoing.oss-cn-beijing.aliyuncs.com/img/ftms/20210204213027215-780.png","province":"110000","city":"110100","county":"110101","address":"2321312","maximumEnrolment":100,"maxinumCustomer":2,"applyBeginTime":"2021-02-04 21:31:00","applyEndTime":"2021-02-18 17:17:00","checkStatus":1,"checkOpinion":"123","contentType":1,"activityIntro":"西南大区活动"},{"id":"ff808081776c490f01776d1a698d0027","createTime":"2021-02-04 20:53:03","name":"20222","activityType":2,"activityNature":2,"agentId":"8ebf39cd6ee311e89d6c00163e03d68e","agentName":"北京三元桥","beginTime":"2021-02-04 20:53:00","endTime":"2021-02-28 18:18:00","showStatus":1,"lineType":1,"onlineTime":"2021-02-04 20:53:03","isLine":1,"href":"<p>202220222022202220222022<br><\/p>","img":"https://foxgoing.oss-cn-beijing.aliyuncs.com/img/ftms/20210204205223272-113.png","province":"110000","city":"110100","county":"110101","address":"2022","maximumEnrolment":100,"maxinumCustomer":2,"applyBeginTime":"2021-02-04 20:53:00","applyEndTime":"2021-02-26 14:14:00","checkStatus":1,"checkOpinion":"2022","contentType":1,"activityIntro":"2022"},{"id":"ff808081776c490f01776d15c6160026","createTime":"2021-02-04 20:47:59","name":"2022","activityType":2,"activityNature":2,"agentId":"8ebf39cd6ee311e89d6c00163e03d68e","agentName":"北京三元桥","beginTime":"2021-02-04 20:49:00","endTime":"2021-02-06 23:11:00","showStatus":1,"lineType":1,"onlineTime":"2021-02-04 20:47:59","isLine":1,"href":"https://www.baidu.com/","img":"https://foxgoing.oss-cn-beijing.aliyuncs.com/img/ftms/20210204204701434-249.png","province":"110000","city":"110100","county":"110105","address":"21312321312","maximumEnrolment":100,"maxinumCustomer":2,"applyBeginTime":"2021-02-04 20:48:00","applyEndTime":"2021-02-05 23:22:00","checkStatus":1,"checkOpinion":"2022","contentType":2,"activityIntro":"v2022"},{"id":"ff808081758792380175879de5760002","createTime":"2020-11-02 14:21:13","name":"测试云店分享1102","activityType":2,"activityNature":2,"agentId":"ff8080817181b982017181f0a1030003","agentName":"测试门店1","beginTime":"2020-11-02 14:21:00","endTime":"2021-03-31 21:21:00","showStatus":1,"lineType":1,"onlineTime":"2020-11-02 14:21:13","isLine":1,"href":"http://ftmsman.dijiahuche.com/lotteryActivity","img":"https://foxgoing.oss-cn-beijing.aliyuncs.com/img/ftms/20201102141958025-800.jpg","province":"120000","city":"120100","county":"120112","address":"朝阳区常营三间房","maximumEnrolment":10,"maxinumCustomer":10,"applyBeginTime":"2020-11-02 14:22:00","applyEndTime":"2020-11-30 21:21:00","checkStatus":1,"checkOpinion":"","contentType":2,"activityIntro":"测试云店分享1102","weChatProgramCodeUrl":"https://foxgoing.oss-cn-beijing.aliyuncs.com/efb500b8-e884-492a-a802-aa4721f4461b.png"}]
         */

        private int count;
        private List<Data> data;

        public int getCount() {
            return count;
        }

        public void setCount(int count) {
            this.count = count;
        }

        public List<Data> getData() {
            return data;
        }

        public void setData(List<Data> data) {
            this.data = data;
        }

        public static class Data extends BaseEntity {
            /**
             * id : ff808081776c490f01776d3d82aa0028
             * createTime : 2021-02-04 21:31:23
             * name : 西南大区活动
             * activityType : 2
             * activityNature : 2
             * agentId : 8ebf39cd6ee311e89d6c00163e03d68e
             * agentName : 北京三元桥
             * beginTime : 2021-02-04 21:31:00
             * endTime : 2021-02-20 21:30:00
             * showStatus : 1
             * lineType : 1
             * onlineTime : 2021-02-04 21:31:23
             * isLine : 1
             * href : <p><img src="https://foxgoing.oss-cn-beijing.aliyuncs.com/img/ftms/20210204213041415-943.png" style=""><br></p>
             * img : https://foxgoing.oss-cn-beijing.aliyuncs.com/img/ftms/20210204213027215-780.png
             * province : 110000
             * city : 110100
             * county : 110101
             * address : 2321312
             * maximumEnrolment : 100
             * maxinumCustomer : 2
             * applyBeginTime : 2021-02-04 21:31:00
             * applyEndTime : 2021-02-18 17:17:00
             * checkStatus : 1
             * checkOpinion : 123
             * contentType : 1
             * activityIntro : 西南大区活动
             * weChatProgramCodeUrl : https://foxgoing.oss-cn-beijing.aliyuncs.com/efb500b8-e884-492a-a802-aa4721f4461b.png
             */

            private String id;
            private String createTime;
            private String name;
            private int activityType;
            private int activityNature;
            private String agentId;
            private String agentName;
            private String beginTime;
            private String endTime;
            private int showStatus;
            private int lineType;
            private String onlineTime;
            private int isLine;
            private String href;
            private String img;
            private String province;
            private String city;
            private String county;
            private String address;
            private int maximumEnrolment;
            private int maxinumCustomer;
            private String applyBeginTime;
            private String applyEndTime;
            private int checkStatus;
            private String checkOpinion;
            private int contentType;
            private String activityIntro;
            private String weChatProgramCodeUrl;

            public String getId() {
                return id;
            }

            public void setId(String id) {
                this.id = id;
            }

            public String getCreateTime() {
                return createTime;
            }

            public void setCreateTime(String createTime) {
                this.createTime = createTime;
            }

            public String getName() {
                return name;
            }

            public void setName(String name) {
                this.name = name;
            }

            public int getActivityType() {
                return activityType;
            }

            public void setActivityType(int activityType) {
                this.activityType = activityType;
            }

            public int getActivityNature() {
                return activityNature;
            }

            public void setActivityNature(int activityNature) {
                this.activityNature = activityNature;
            }

            public String getAgentId() {
                return agentId;
            }

            public void setAgentId(String agentId) {
                this.agentId = agentId;
            }

            public String getAgentName() {
                return agentName;
            }

            public void setAgentName(String agentName) {
                this.agentName = agentName;
            }

            public String getBeginTime() {
                return beginTime;
            }

            public void setBeginTime(String beginTime) {
                this.beginTime = beginTime;
            }

            public String getEndTime() {
                return endTime;
            }

            public void setEndTime(String endTime) {
                this.endTime = endTime;
            }

            public int getShowStatus() {
                return showStatus;
            }

            public void setShowStatus(int showStatus) {
                this.showStatus = showStatus;
            }

            public int getLineType() {
                return lineType;
            }

            public void setLineType(int lineType) {
                this.lineType = lineType;
            }

            public String getOnlineTime() {
                return onlineTime;
            }

            public void setOnlineTime(String onlineTime) {
                this.onlineTime = onlineTime;
            }

            public int getIsLine() {
                return isLine;
            }

            public void setIsLine(int isLine) {
                this.isLine = isLine;
            }

            public String getHref() {
                return href;
            }

            public void setHref(String href) {
                this.href = href;
            }

            public String getImg() {
                return img;
            }

            public void setImg(String img) {
                this.img = img;
            }

            public String getProvince() {
                return province;
            }

            public void setProvince(String province) {
                this.province = province;
            }

            public String getCity() {
                return city;
            }

            public void setCity(String city) {
                this.city = city;
            }

            public String getCounty() {
                return county;
            }

            public void setCounty(String county) {
                this.county = county;
            }

            public String getAddress() {
                return address;
            }

            public void setAddress(String address) {
                this.address = address;
            }

            public int getMaximumEnrolment() {
                return maximumEnrolment;
            }

            public void setMaximumEnrolment(int maximumEnrolment) {
                this.maximumEnrolment = maximumEnrolment;
            }

            public int getMaxinumCustomer() {
                return maxinumCustomer;
            }

            public void setMaxinumCustomer(int maxinumCustomer) {
                this.maxinumCustomer = maxinumCustomer;
            }

            public String getApplyBeginTime() {
                return applyBeginTime;
            }

            public void setApplyBeginTime(String applyBeginTime) {
                this.applyBeginTime = applyBeginTime;
            }

            public String getApplyEndTime() {
                return applyEndTime;
            }

            public void setApplyEndTime(String applyEndTime) {
                this.applyEndTime = applyEndTime;
            }

            public int getCheckStatus() {
                return checkStatus;
            }

            public void setCheckStatus(int checkStatus) {
                this.checkStatus = checkStatus;
            }

            public String getCheckOpinion() {
                return checkOpinion;
            }

            public void setCheckOpinion(String checkOpinion) {
                this.checkOpinion = checkOpinion;
            }

            public int getContentType() {
                return contentType;
            }

            public void setContentType(int contentType) {
                this.contentType = contentType;
            }

            public String getActivityIntro() {
                return activityIntro;
            }

            public void setActivityIntro(String activityIntro) {
                this.activityIntro = activityIntro;
            }

            public String getWeChatProgramCodeUrl() {
                return weChatProgramCodeUrl;
            }

            public void setWeChatProgramCodeUrl(String weChatProgramCodeUrl) {
                this.weChatProgramCodeUrl = weChatProgramCodeUrl;
            }
        }
    }
}
