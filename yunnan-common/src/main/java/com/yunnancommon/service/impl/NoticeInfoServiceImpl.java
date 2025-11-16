package com.yunnancommon.service.impl;


import com.yunnancommon.entity.dto.NoticeInfoDto;
import com.yunnancommon.entity.po.NoticeReadInfo;
import com.yunnancommon.entity.query.SimplePage;
import com.yunnancommon.entity.vo.CurrentVO;
import com.yunnancommon.enums.DateTimePatternEnum;
import com.yunnancommon.enums.PageSize;
import com.yunnancommon.entity.vo.PaginationResultVO;
import com.yunnancommon.entity.po.NoticeInfo;
import com.yunnancommon.entity.query.NoticeInfoQuery;
import com.yunnancommon.mapper.NoticeInfoMapper;
import com.yunnancommon.mapper.NoticeReadInfoMapper;
import com.yunnancommon.service.NoticeInfoService;
import com.yunnancommon.service.NoticeReadInfoService;
import com.yunnancommon.utils.DateUtils;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;

import java.util.Date;
import java.util.List;
/**
 * @Description:通知消息表ServiceImpl
 * @auther:group2
 * @date:2025/10/22
 */
@Service("noticeInfoService")
public class NoticeInfoServiceImpl implements NoticeInfoService {

	@Resource
	private NoticeInfoMapper<NoticeInfo, NoticeInfoQuery> noticeInfoMapper;

	@Resource
	private NoticeReadInfoService noticeReadInfoService;

	@Resource
	private NoticeReadInfoMapper<NoticeReadInfo, Object> noticeReadInfoMapper;

	/**
	 * 根据条件查询列表
	 */
	@Override
	public List<NoticeInfo> findListByParam(NoticeInfoQuery query) {
		return this.noticeInfoMapper.selectList(query);
	}

	/**
	 * 根据条件查询数量
	 */
	@Override
	public Integer findCountByParam(NoticeInfoQuery query) {
		return this.noticeInfoMapper.selectCount(query);
	}

	/**
	 * 分页查询
	 */
	@Override
	public PaginationResultVO<NoticeInfo> findListByPage(NoticeInfoQuery query) {
		Integer count = this.findCountByParam(query);
		Integer pageSize = query.getPageSize() == null ? PageSize.SIZE15.getSize(): query.getPageSize();
		SimplePage page = new SimplePage(query.getPageNo(), count, pageSize);
		query.setSimplePage(page);
		List<NoticeInfo> list = this.findListByParam(query);
		PaginationResultVO<NoticeInfo> result = new PaginationResultVO<NoticeInfo>(count, page.getPageSize(), page.getPageNo(), page.getPageTotal(), list);
		return result;
	}

	/**
	 * 新增
	 */
	@Override
	public Integer add(NoticeInfo bean) {
		return this.noticeInfoMapper.insert(bean);
	}

	/**
	 * 批量新增
	 */
	@Override
	public Integer addBatch(List<NoticeInfo> listBean) {
		if(listBean == null || listBean.isEmpty()) {
			return 0;
		}
		return this.noticeInfoMapper.insertBatch(listBean);
	}

	/**
	 * 批量新增或修改
	 */
	@Override
	public Integer addOrUpdateBatch(List<NoticeInfo> listBean) {
		if(listBean == null || listBean.isEmpty()) {
			return 0;
		}
		return this.noticeInfoMapper.insertOrUpdateBatch(listBean);
	}

	/**
	 * 根据NoticeId查询
	 */
	@Override
	public NoticeInfo getNoticeInfoByNoticeId(Long noticeId) {
		return this.noticeInfoMapper.selectByNoticeId(noticeId);
	}

	/**
	 * 根据NoticeId更新
	 */
	@Override
	public Integer updateNoticeInfoByNoticeId(NoticeInfo bean, Long noticeId) {
		return this.noticeInfoMapper.updateByNoticeId(bean, noticeId);
	}

	/**
	 * 根据NoticeId删除
	 */
	@Override
	public Integer deleteNoticeInfoByNoticeId(Long noticeId) {
		return this.noticeInfoMapper.deleteByNoticeId(noticeId);
	}

	@Override
	public void getCurrentNoticeInfo(String username, CurrentVO currentVO) {
		String curTime = DateUtils.format(new Date(), DateTimePatternEnum.YYYY_MM_DD.getPattern());
		List<NoticeInfoDto> noticeInfoDtoList = this.noticeInfoMapper.selectListWithReadStatus(curTime, username);
        currentVO.setNoticeInfoList(noticeInfoDtoList);
        currentVO.setNoReadCount(this.noticeInfoMapper.countNoReadNoticeInfoByUsername(curTime,username));
	}
}