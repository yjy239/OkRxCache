package com.yjy.okrxcache;

import android.support.annotation.RestrictTo;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.List;

import static android.support.annotation.RestrictTo.Scope.LIBRARY_GROUP;

/**
 * Created by huyongqiang on 15/12/15.
 * edited by chenfm01 on 16/6/15
 */
@RestrictTo(LIBRARY_GROUP)
public class CommonDictResponse extends Response {

    private Result result;

    public Result getResult() {
        return this.result;
    }

    public void setResult(Result result) {
        this.result = result;
    }

    public class Result {
        @SerializedName("invite_identity")
        public List<VerifyStatus> inviteIdentity;
        @SerializedName("extra_button")
        public ExtraButton extraButton;
        @SerializedName("launch_page")
        public LaunchPage launchPage;
        @SerializedName("blackpearl_url")
        public String blackPearlUrl;
        @SerializedName("house_intake_status")
        public List<VerifyStatus> houseIntakeStatus;
        @SerializedName("qiniu_host")
        public String qiniuHost;
        @SerializedName("house_identity")
        public List<VerifyStatus> houseIdentity;
        @SerializedName("is_bot_open")
        public boolean is_bot_open;
        /**
         * 是否开通物业缴费服务
         */
        @SerializedName("has_service_fee_function")
        public boolean hasServiceFeeFunction;
        @SerializedName("what_can_i_do_url")
        public String whatCanIDoUrl;
        //阳光物业Url
        @SerializedName("sunshine_service_url")
        public String sunshineServiceUrl;
        //阳光物业Url
        @SerializedName("sunshine_fm_url")
        public String sunshineFMUrl;
        //个人账户页
        @SerializedName("account_url")
        public String accountUrl;
        //本来生活页
        @SerializedName("benlai_url")
        public String benlaiUrl;
        //本来生活页
        @SerializedName("mine_order_url")
        public String orderUrl;
        /**
         * 二手聊天中，举报卖家的url
         */
        @SerializedName("report_user_url")
        public String reportUserUrl;

        /***************打赏**********************/
        //打赏 最大值
        @SerializedName("reward_max")
        public String rewardMax;
        //打赏 提示语
        @SerializedName("reward_hint")
        public String rewardHint;
        //打赏 消息
        @SerializedName("reward_message")
        public String rewardMessage;

        //二手市场看更多
        @SerializedName("secondary_url")
        public String secondaryUrl;
        //我的二手
        @SerializedName("my_secondary_url")
        public String mySecondaryUrl;
        @SerializedName("front_white_list")
        public List<String> whiteList;
    }

    public class VerifyStatus implements Serializable {
        public int state;
        public String name;

        public int getState() {
            return state;
        }

        public void setState(int state) {
            this.state = state;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        @Override
        public String toString() {
            return "VerifyStatus{" +
                    "state=" + state +
                    ", name='" + name + '\'' +
                    '}';
        }
    }

    public class ExtraButton {
        public String url;
        public String text;

        public String getUrl() {
            return url;
        }

        public void setUrl(String url) {
            this.url = url;
        }

        public String getText() {
            return text;
        }

        public void setText(String text) {
            this.text = text;
        }

        @Override
        public String toString() {
            return "ExtraButton{" +
                    "url='" + url + '\'' +
                    ", text='" + text + '\'' +
                    '}';
        }
    }

    public class LaunchPage {
        public String image;
        @SerializedName("action_type")
        public String actionType;
        @SerializedName("action_id")
        public String actionId;
    }

}
