package com.kongyun.chexibao.home.activity;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;

import com.hyphenate.EMMessageListener;
import com.hyphenate.chat.EMMessage;
import com.hyphenate.easeui.ui.EaseChatFragment;
import com.hyphenate.easeui.ui.EaseChatFragment.EaseChatFragmentHelper;
import com.hyphenate.easeui.widget.chatrow.EaseCustomChatRowProvider;
import com.kongyun.chexibao.AppUtils;
import com.kongyun.chexibao.R;
import com.kongyun.chexibao.ui.base.BaseActivity;
import com.kongyun.chexibao.ui.base.BasePresenter;
import com.kongyun.chexibao.utils.LogUtil;
import com.kongyun.chexibao.utils.SPUtils;

/**
 * Created by Zhucmao on 2018/3/28 17:34.
 * TODO:聊天对话框
 */

public class ChatActivity extends BaseActivity {
    private String logo;
    private static String name;
    private String logoPath;
    private String tradeName;
    public static ChatActivity activityInstance;
    private Bundle extras;
    @Override
    protected int getLayoutRes() {
        return R.layout.activity_chat;
    }

    @Override
    public void initView() {
        super.initView();
        //从消息列表获取头像和昵称
        if (TextUtils.isEmpty(getIntent().getStringExtra(AppUtils.MESSAGE_ATTR_FORM_PIC))){
            logo = getIntent().getStringExtra(AppUtils.MESSAGE_ATTR_TO_PIC);
            name = getIntent().getStringExtra(AppUtils.MESSAGE_ATTR_TO_NICK);
        } else{
            logo = getIntent().getStringExtra(AppUtils.MESSAGE_ATTR_FORM_PIC);
            name = getIntent().getStringExtra(AppUtils.MESSAGE_ATTR_FORM_NICK);
        }

        //并从本地取出我的头像
        logoPath = (String) SPUtils.getSp(SPUtils.avator,"file:///android_asset/logo_android1.png");
        tradeName = (String) SPUtils.getSp(SPUtils.nickName,"");
        LogUtil.i("ChatActivity","headImg=="+logoPath+"tradeName=="+tradeName+"logo=="+logo+"name=="+name);

        //将环信的聊天界面chatFragment集成进来
        initHx();
       /* EaseChatFragment easeChatFragment = new EaseChatFragment();  //环信聊天界面
        easeChatFragment.setArguments(getIntent().getExtras()); //需要的参数
        getSupportFragmentManager().beginTransaction().add(R.id.layout_chat,easeChatFragment).commit();  //Fragment切换*/
    }
    @Override
    protected BasePresenter createPresenter() {
        return null;
    }


    //给扩展属性设置头像和昵称。
     EaseChatFragmentHelper helper = new  EaseChatFragmentHelper() {
        @Override
        public void onSetMessageAttributes(EMMessage message) {
            // 附带扩展属性，头像和昵称他人的
            message.setAttribute(AppUtils.MESSAGE_ATTR_TO_PIC, logo);
            message.setAttribute(AppUtils.MESSAGE_ATTR_TO_NICK, name);

            //我的头像  存到自己的文件中
            message.setAttribute(AppUtils.MESSAGE_ATTR_FORM_PIC, logoPath);
            message.setAttribute(AppUtils.MESSAGE_ATTR_FORM_NICK, tradeName);
        }
        @Override
        public void onEnterToChatDetails() {
        }
        @Override
        public void onAvatarClick(String username) {
        }
        @Override
        public void onAvatarLongClick(String username) {
        }
        @Override
        public boolean onMessageBubbleClick(EMMessage message) {
            return false;
        }
        @Override
        public void onMessageBubbleLongClick(EMMessage message) {
        }
        @Override
        public boolean onExtendMenuItemClick(int itemId, View view) {
            return false;
        }
        @Override
        public EaseCustomChatRowProvider onSetCustomChatRowProvider() {
            return null;
        }
    };
    public void initHx() {
        activityInstance = this;
        ChatFragment chat = new ChatFragment();
        //获取从上个界面获取的参数，传给聊天界面。
        extras = getIntent().getExtras();
        chat.setArguments(extras);
        //这个监听是接收到消息就给消息设置拓展属性。
         chat.setChatFragmentHelper(helper);
        getSupportFragmentManager().beginTransaction().add(R.id.layout_chat, chat).commit();

    }

    /**
     * 继承环信的聊天页面
     * 实现消息监听和聊天页面的标题上显示的对方昵称
     */
    public static class ChatFragment extends EaseChatFragment implements EMMessageListener {
        /**
         * 设置聊天页面的title上面的昵称
         */
        @Override
        protected void setUpView() {
            super.setUpView();
            titleBar.setTitle(name);

        }

    }
}

