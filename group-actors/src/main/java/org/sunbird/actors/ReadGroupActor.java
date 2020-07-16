package org.sunbird.actors;

import java.util.*;
import org.apache.commons.lang3.StringUtils;
import org.sunbird.actor.core.ActorConfig;
import org.sunbird.message.ResponseCode;
import org.sunbird.models.GroupResponse;
import org.sunbird.models.MemberResponse;
import org.sunbird.request.Request;
import org.sunbird.response.Response;
import org.sunbird.service.GroupService;
import org.sunbird.service.GroupServiceImpl;
import org.sunbird.util.CacheUtil;
import org.sunbird.util.JsonKey;
import org.sunbird.util.JsonUtils;

@ActorConfig(
  tasks = {"readGroup"},
  asyncTasks = {},
  dispatcher = "group-dispatcher"
)
public class ReadGroupActor extends BaseActor {

  @Override
  public void onReceive(Request request) throws Throwable {
    String operation = request.getOperation();
    switch (operation) {
      case "readGroup":
        readGroup(request);
        break;
      default:
        onReceiveUnsupportedMessage("ReadGroupActor");
    }
  }
  /**
   * This method will read group in cassandra based on group id.
   *
   * @param actorMessage
   */
  private void readGroup(Request actorMessage) throws Exception {
    logger.info("ReadGroup method call");
    CacheUtil cacheUtil = new CacheUtil();
    GroupService groupService = new GroupServiceImpl();
    String groupId = (String) actorMessage.getRequest().get(JsonKey.GROUP_ID);
    List<String> requestFields = (List<String>) actorMessage.getRequest().get(JsonKey.FIELDS);
    GroupResponse groupResponse;
    String groupInfo = cacheUtil.getCache(groupId);
    if (StringUtils.isNotEmpty(groupInfo)) {
      groupResponse = JsonUtils.deserialize(groupInfo, GroupResponse.class);
    } else {
      groupResponse = groupService.readGroup(groupId);
      cacheUtil.setCache(groupId, JsonUtils.serialize(groupResponse));
    }
    if (requestFields.contains(JsonKey.MEMBERS)) {
      String groupMember = cacheUtil.getCache(constructRedisIdentifier(groupId));
      List<MemberResponse> memberResponses = new ArrayList<>();
      if (StringUtils.isNotEmpty(groupMember)) {
        memberResponses = JsonUtils.deserialize(groupMember, memberResponses.getClass());
      } else {
        memberResponses = groupService.readGroupMembers(groupId);
        cacheUtil.setCache(constructRedisIdentifier(groupId), JsonUtils.serialize(memberResponses));
      }
      groupResponse.setMembers(memberResponses);
    }
    if (!requestFields.contains(JsonKey.ACTIVITIES)) {
      groupResponse.setActivities(null);
    }
    Response response = new Response(ResponseCode.OK.getCode());
    Map<String, Object> map = JsonUtils.convert(groupResponse, Map.class);
    response.putAll(map);
    sender().tell(response, self());
  }

  /**
   * constructs redis identifie for group & members info groupId_members
   *
   * @param groupId
   * @return
   */
  private String constructRedisIdentifier(String groupId) {
    return groupId + "_" + JsonKey.MEMBERS;
  }
}
