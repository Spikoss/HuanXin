package com.kongyun.chexibao.home.activity;


import android.content.Intent;

import com.hyphenate.chat.EMClient;
import com.hyphenate.chat.EMConversation;
import com.hyphenate.chat.EMMessage;
import com.hyphenate.easeui.EaseConstant;
import com.hyphenate.easeui.domain.EaseUser;
import com.hyphenate.easeui.ui.EaseContactListFragment;
import com.hyphenate.easeui.ui.EaseConversationListFragment;
import com.hyphenate.exceptions.HyphenateException;
import com.kongyun.chexibao.AppUtils;
import com.kongyun.chexibao.R;
import com.kongyun.chexibao.runtimepermissions.PermissionsManager;
import com.kongyun.chexibao.runtimepermissions.PermissionsResultAction;
import com.kongyun.chexibao.ui.base.BaseActivity;
import com.kongyun.chexibao.ui.base.BasePresenter;
import com.kongyun.chexibao.utils.LogUtil;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 加载联系人列表
 */
public class ChatListActivity extends BaseActivity {
    @Override
    protected int getLayoutRes() {
        return R.layout.activity_chatlist;
    }

    @Override
    public void initView() {
        super.initView();

        PermissionsManager.getInstance().requestAllManifestPermissionsIfNecessary(this, new PermissionsResultAction() {
            @Override
            public void onGranted() {
                // ToastUtil.showToast("Granted");
            }

            @Override
            public void onDenied(String permission) {
                //  ToastUtil.showToast("Denied");
            }
        });

        //会话列表
        final EaseConversationListFragment conversationListFragment = new EaseConversationListFragment();

        //联系人列表
        final EaseContactListFragment contactListFragment = new EaseContactListFragment();
        new Thread() {//需要在子线程中调用
            @Override
            public void run() {
                //需要设置联系人列表才能启动fragment
                contactListFragment.setContactsMap(getContact());
                conversationListFragment.setConversationListItemClickListener(new EaseConversationListFragment.EaseConversationListItemClickListener() {
                    @Override
                    public void onListItemClicked(EMConversation conversation) {
                        Intent intent = new Intent(ChatListActivity.this, ChatActivity.class);
                        intent.putExtra(EaseConstant.EXTRA_USER_ID, conversation.conversationId() );  //对方账号
                        /**
                         * 从会话列表跳转到聊天页面
                         * 首先要判断最后一条信息是接收还是发送消息
                         * 然后分情况传递头像和昵称
                         *
                         */

                        EMMessage lastMessage = conversation.getLastMessage();

                        if (lastMessage.direct() == EMMessage.Direct.RECEIVE) {
                            try {
                                intent.putExtra(AppUtils.MESSAGE_ATTR_FORM_PIC, lastMessage.getStringAttribute(AppUtils.MESSAGE_ATTR_FORM_PIC));
                                intent.putExtra(AppUtils.MESSAGE_ATTR_FORM_NICK, lastMessage.getStringAttribute(AppUtils.MESSAGE_ATTR_FORM_NICK));
                            } catch (HyphenateException e) {
                                e.printStackTrace();
                            }
                        } else {
                            try {
                                intent.putExtra(AppUtils.MESSAGE_ATTR_TO_PIC, lastMessage.getStringAttribute(AppUtils.MESSAGE_ATTR_TO_PIC));
                                intent.putExtra(AppUtils.MESSAGE_ATTR_TO_NICK, lastMessage.getStringAttribute(AppUtils.MESSAGE_ATTR_TO_NICK));
                            } catch (HyphenateException e) {
                                e.printStackTrace();
                            }
                        }

                        startActivity(intent);
                        overridePendingTransition(R.anim.in_from_right, R.anim.out_to_left);
                    }
                });
                getSupportFragmentManager().beginTransaction().add(R.id.layout_chatList, conversationListFragment).commit();
            }
        }.start();

    }

    @Override
    protected BasePresenter createPresenter() {
        return null;
    }

    /**
     * 加载联系人
     *
     * @return map
     */
    private Map<String, EaseUser> getContact() {
        Map<String, EaseUser> map = new HashMap<>();
        try {
            List<String> userNames = EMClient.getInstance().contactManager().getAllContactsFromServer();
            LogUtil.i("ChatListActivity", userNames.size() + "======");
            for (String userId :
                    userNames) {
                map.put(userId, new EaseUser(userId));
            }
        } catch (HyphenateException e) {
            e.printStackTrace();
        }
        return map;
    }

}
