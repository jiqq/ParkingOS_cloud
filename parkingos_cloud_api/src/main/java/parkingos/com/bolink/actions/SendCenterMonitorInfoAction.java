package parkingos.com.bolink.actions;

import com.zld.common_dao.dao.CommonDao;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import parkingos.com.bolink.beans.ParkTokenTb;
import parkingos.com.bolink.utlis.CommonUtils;
import parkingos.com.bolink.utlis.RequestUtil;
import parkingos.com.bolink.utlis.StringUtils;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.net.URLDecoder;
import java.util.HashMap;
import java.util.Map;

@Controller
public class SendCenterMonitorInfoAction {

	Logger logger = Logger.getLogger(UploadCarPics.class);
	@Autowired
	CommonDao commonDao;
	@Autowired
	CommonUtils commonUtils;

	@RequestMapping(value = "/centermonitor.do")
	public void sendmsgtopark(HttpServletRequest request, HttpServletResponse response) throws Exception{
		String action = request.getParameter("action");
		if(action == null){
			action = "";
		}else if(action.equals("balanceOrderInfo")){
			logger.info("进入处理确认事件====>>>>");
			String orderId = RequestUtil.getString(request, "order_id");
			String carNumber = URLDecoder.decode(RequestUtil.getString(request, "car_number"),"UTF-8");
			String channel_id = StringUtils.decodeUTF8(RequestUtil.getString(request, "channel_id"));
			String event_id = StringUtils.decodeUTF8(RequestUtil.getString(request, "event_id"));
			String comid = RequestUtil.getString(request, "comid");
			logger.info("进入处理确认事件====>>>>"+channel_id+"~~~~"+carNumber+"~~~"+event_id);
			boolean isSend = false;
			ParkTokenTb tokenTb = commonUtils.getChannelInfo(comid,channel_id);//commonUtils.getChannel(Long.valueOf(comid));
			if(tokenTb!=null){
				Map<String,Object> params = new HashMap<String,Object>();
				params.put("service_name", "confirm_order_inform");
				//params.put("balance_time", TimeTools.getLongMilliSeconds());
				params.put("channel_id",channel_id);
				params.put("order_id", orderId);
				params.put("car_number", carNumber);
				params.put("comid", comid);
				params.put("event_id",event_id);
				logger.error("balanceOrderInfo>>>params:"+params);
				String mesg = StringUtils.createJson(params);
				isSend=commonUtils.doSendMessage(mesg,tokenTb);//
				logger.error("发送确认订单通知到SDK："+mesg+",ret:"+isSend);
				StringUtils.ajaxOutput(response,"1");
			}else{
				logger.error("确认订单通知没有匹配到通道，发送失败");
			}
		}
	}


	/**
	 * 获取下发数据的TCP通道
	 * @param comid
	 * @return
	 */
	/*private Channel getChannel(String comid){
		String channelPass = "";
		ParkTokenTb parkTokenConditions = new ParkTokenTb();
		parkTokenConditions.setParkId(comid);
		PageOrderConfig pageOrderConfig = new PageOrderConfig();
		pageOrderConfig.setOrderInfo("id","desc");
		List<ParkTokenTb> parkTokenTbs = (List<ParkTokenTb>)commonDao.selectListByConditions(parkTokenConditions,pageOrderConfig);
		if(parkTokenTbs != null && !parkTokenTbs.isEmpty()){
			ParkTokenTb parkTokenTb = parkTokenTbs.get(0);
			String localId = parkTokenTb.getLocalId();
			if(!Check.isEmpty(localId)){
				channelPass = comid+"_"+localId;
			}
			Channel channel = NettyChannelMap.gets(channelPass);
			if (channel != null && channel.isActive()&& channel.isWritable()) {
				logger.error("sdk comid:"+channelPass);
				return channel;
			}
		}
		return null;
	}*/
}
