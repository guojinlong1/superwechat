/**
 * Copyright (C) 2013-2014 EaseMob Technologies. All rights reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *     http://www.apache.org/licenses/LICENSE-2.0
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package cn.ucai.superwechat.adapter;

import java.util.List;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.ContentValues;
import android.content.Context;
import android.text.TextUtils;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Response;
import com.android.volley.toolbox.NetworkImageView;
import com.easemob.chat.EMChatManager;
import com.easemob.chat.EMGroupManager;
import com.easemob.exceptions.EaseMobException;

import cn.ucai.superwechat.I;
import cn.ucai.superwechat.activity.NewFriendsMsgActivity;
import cn.ucai.superwechat.bean.Group;
import cn.ucai.superwechat.bean.Member;
import cn.ucai.superwechat.bean.User;
import cn.ucai.superwechat.data.ApiParams;
import cn.ucai.superwechat.data.GsonRequest;
import cn.ucai.superwechat.db.InviteMessgeDao;
import cn.ucai.superwechat.domain.InviteMessage;
import cn.ucai.superwechat.domain.InviteMessage.InviteMesageStatus;
import cn.ucai.superwechat.task.DownloadGroupMemberTask;
import cn.ucai.superwechat.utils.UserUtils;

public class NewFriendsMsgAdapter extends ArrayAdapter<InviteMessage> {

	private Context context;
	private InviteMessgeDao messgeDao;
	ProgressDialog pd;

	public NewFriendsMsgAdapter(Context context, int textViewResourceId, List<InviteMessage> objects) {
		super(context, textViewResourceId, objects);
		this.context = context;
		messgeDao = new InviteMessgeDao(context);
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		final ViewHolder holder;
		if (convertView == null) {
			holder = new ViewHolder();
			convertView = View.inflate(context, cn.ucai.superwechat.R.layout.row_invite_msg, null);
			holder.avator = (NetworkImageView) convertView.findViewById(cn.ucai.superwechat.R.id.avatar);
			holder.reason = (TextView) convertView.findViewById(cn.ucai.superwechat.R.id.message);
			holder.name = (TextView) convertView.findViewById(cn.ucai.superwechat.R.id.name);
			holder.status = (Button) convertView.findViewById(cn.ucai.superwechat.R.id.user_state);
			holder.groupContainer = (LinearLayout) convertView.findViewById(cn.ucai.superwechat.R.id.ll_group);
			holder.groupname = (TextView) convertView.findViewById(cn.ucai.superwechat.R.id.tv_groupName);
			// holder.time = (TextView) convertView.findViewById(R.id.time);
			convertView.setTag(holder);
		} else {
			holder = (ViewHolder) convertView.getTag();
		}
		
		String str1 = context.getResources().getString(cn.ucai.superwechat.R.string.Has_agreed_to_your_friend_request);
		String str2 = context.getResources().getString(cn.ucai.superwechat.R.string.agree);
		
		String str3 = context.getResources().getString(cn.ucai.superwechat.R.string.Request_to_add_you_as_a_friend);
		String str4 = context.getResources().getString(cn.ucai.superwechat.R.string.Apply_to_the_group_of);
		String str5 = context.getResources().getString(cn.ucai.superwechat.R.string.Has_agreed_to);
		String str6 = context.getResources().getString(cn.ucai.superwechat.R.string.Has_refused_to);
		final InviteMessage msg = getItem(position);
		if (msg != null) {
			if(msg.getGroupId() != null){ // 显示群聊提示
				holder.groupContainer.setVisibility(View.VISIBLE);
				holder.groupname.setText(msg.getGroupName());
			} else{
				holder.groupContainer.setVisibility(View.GONE);
			}
			
			holder.reason.setText(msg.getReason());
			//holder.name.setText(msg.getFrom());
			// holder.time.setText(DateUtils.getTimestampString(new
			// Date(msg.getTime())));
			if (msg.getStatus() == InviteMesageStatus.BEAGREED) {
				holder.status.setVisibility(View.INVISIBLE);
				holder.reason.setText(str1);
			} else if (msg.getStatus() == InviteMesageStatus.BEINVITEED || msg.getStatus() == InviteMesageStatus.BEAPPLYED) {
				holder.status.setVisibility(View.VISIBLE);
				holder.status.setEnabled(true);
				holder.status.setBackgroundResource(android.R.drawable.btn_default);
				holder.status.setText(str2);
				if(msg.getStatus() == InviteMesageStatus.BEINVITEED){
					if (msg.getReason() == null) {
						// 如果没写理由
						holder.reason.setText(str3);
					}
				}else{ //入群申请
					if (TextUtils.isEmpty(msg.getReason())) {
						holder.reason.setText(str4 + msg.getGroupName());
					}
				}
				// 设置点击事件
				holder.status.setOnClickListener(new OnClickListener() {

					@Override
					public void onClick(View v) {
						// 同意别人发的好友请求
						acceptInvitation(holder.status, msg);
					}
				});
			} else if (msg.getStatus() == InviteMesageStatus.AGREED) {
				holder.status.setText(str5);
				holder.status.setBackgroundDrawable(null);
				holder.status.setEnabled(false);
			} else if(msg.getStatus() == InviteMesageStatus.REFUSED){
				holder.status.setText(str6);
				holder.status.setBackgroundDrawable(null);
				holder.status.setEnabled(false);
			}

			// 设置用户头像
			UserUtils.setUserAvatar(UserUtils.getAvatarPath(msg.getFrom()),holder.avator);
			try {
				String path = new ApiParams()
                        .with(I.User.USER_NAME,msg.getFrom())
                        .getRequestUrl(I.REQUEST_FIND_USER);
				((NewFriendsMsgActivity)context).executeRequest(new GsonRequest<User>(path,User.class,
						responesFindUserListener(holder.name),((NewFriendsMsgActivity)context).errorListener()));
			} catch (Exception e) {
				e.printStackTrace();
			}
		}

		return convertView;
	}

	private Response.Listener<User> responesFindUserListener(final TextView name) {
		return new Response.Listener<User>() {
			@Override
			public void onResponse(User user) {
				if(user!=null){
					UserUtils.setUserBeanNick(user,name);
				}
			}
		};
	}

	/**
	 * 同意好友请求或者群申请
	 * 
	 * @param button
	 */
	private void acceptInvitation(final Button button, final InviteMessage msg) {
		pd = new ProgressDialog(context);
		String str1 = context.getResources().getString(cn.ucai.superwechat.R.string.Are_agree_with);
		final String str2 = context.getResources().getString(cn.ucai.superwechat.R.string.Has_agreed_to);
		final String str3 = context.getResources().getString(cn.ucai.superwechat.R.string.Agree_with_failure);
		pd.setMessage(str1);
		pd.setCanceledOnTouchOutside(false);
		pd.show();

		new Thread(new Runnable() {
			public void run() {
				// 调用sdk的同意方法
				try {
					if(msg.getGroupId() == null) //同意好友请求
					{
						EMChatManager.getInstance().acceptInvitation(msg.getFrom());
						((Activity) context).runOnUiThread(new Runnable() {

							@Override
							public void run() {
								pd.dismiss();
								button.setText(str2);
								msg.setStatus(InviteMesageStatus.AGREED);
								// 更新db
								ContentValues values = new ContentValues();
								values.put(InviteMessgeDao.COLUMN_NAME_STATUS, msg.getStatus().ordinal());
								messgeDao.updateMessage(msg.getId(), values);
								button.setBackgroundDrawable(null);
								button.setEnabled(false);

							}
						});

					}else {//同意加群申请
						String path = new ApiParams()
								.with(I.Member.USER_NAME,msg.getFrom())
								.with(I.Member.GROUP_HX_ID,msg.getGroupId())
								.getRequestUrl(I.REQUEST_ADD_GROUP_MEMBER_BY_USERNAME);
						((NewFriendsMsgActivity)context).executeRequest(new GsonRequest<Group>(path,Group.class,
								responesAddGroupMemberListener(msg,button),((NewFriendsMsgActivity)context).errorListener()));
						EMGroupManager.getInstance().acceptApplication(msg.getFrom(), msg.getGroupId());
					}

				} catch (final Exception e) {
					((Activity) context).runOnUiThread(new Runnable() {

						@Override
						public void run() {
							pd.dismiss();
							Toast.makeText(context, str3 + e.getMessage(), Toast.LENGTH_LONG).show();
						}
					});

				}
			}
		}).start();
	}

	private Response.Listener<Group> responesAddGroupMemberListener(final InviteMessage msg,final Button button) {
		return new Response.Listener<Group>() {
			@Override
			public void onResponse(Group group) {
				if(group!=null&&group.isResult()){
					final String str2 = context.getResources().getString(cn.ucai.superwechat.R.string.Has_agreed_to);
					new DownloadGroupMemberTask(context,group.getMGroupHxid()).execute();
					try {
						EMGroupManager.getInstance().acceptApplication(msg.getFrom(), msg.getGroupId());
						((Activity) context).runOnUiThread(new Runnable() {

							@Override
							public void run() {
								pd.dismiss();
								button.setText(str2);
								msg.setStatus(InviteMesageStatus.AGREED);
								// 更新db
								ContentValues values = new ContentValues();
								values.put(InviteMessgeDao.COLUMN_NAME_STATUS, msg.getStatus().ordinal());
								messgeDao.updateMessage(msg.getId(), values);
								button.setBackgroundDrawable(null);
								button.setEnabled(false);

							}
						});
					} catch (EaseMobException e) {
						e.printStackTrace();
					}
				}
			}
		};
	}

	private static class ViewHolder {
		NetworkImageView avator;
		TextView name;
		TextView reason;
		Button status;
		LinearLayout groupContainer;
		TextView groupname;
		// TextView time;
	}

}
