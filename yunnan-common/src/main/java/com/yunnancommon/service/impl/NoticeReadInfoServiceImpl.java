package com.yunnancommon.service.impl;


import com.yunnancommon.entity.query.SimplePage;
import com.yunnancommon.enums.PageSize;
import com.yunnancommon.entity.vo.PaginationResultVO;
import com.yunnancommon.entity.po.NoticeReadInfo;
import com.yunnancommon.entity.query.NoticeReadInfoQuery;
import com.yunnancommon.mapper.NoticeReadInfoMapper;
import com.yunnancommon.service.NoticeReadInfoService;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;

import java.util.List;
/**
 * @Description:通知阅读记录表ServiceImpl
 * @auther:group2
 * @date:2025/10/22
 */
@Service("noticeReadInfoService")
public class NoticeReadInfoServiceImpl implements NoticeReadInfoService {

	@Resource
	private NoticeReadInfoMapper<NoticeReadInfo, NoticeReadInfoQuery> noticeReadInfoMapper;

	/**
	 * 根据条件查询列表
	 */
	@Override
	public List<NoticeReadInfo> findListByParam(NoticeReadInfoQuery query) {
		return this.noticeReadInfoMapper.selectList(query);
	}

	/**
	 * 根据条件查询数量
	 */
	@Override
	public Integer findCountByParam(NoticeReadInfoQuery query) {
		return this.noticeReadInfoMapper.selectCount(query);
	}

	/**
	 * 分页查询
	 */
	@Override
	public PaginationResultVO<NoticeReadInfo> findListByPage(NoticeReadInfoQuery query) {
		Integer count = this.findCountByParam(query);
		Integer pageSize = query.getPageSize() == null ? PageSize.SIZE15.getSize(): query.getPageSize();
		SimplePage page = new SimplePage(query.getPageNo(), count, pageSize);
		query.setSimplePage(page);
		List<NoticeReadInfo> list = this.findListByParam(query);
		PaginationResultVO<NoticeReadInfo> result = new PaginationResultVO<NoticeReadInfo>(count, page.getPageSize(), page.getPageNo(), page.getPageTotal(), list);
		return result;
	}

	/**
	 * 新增
	 */
	@Override
	public Integer add(NoticeReadInfo bean) {
		return this.noticeReadInfoMapper.insert(bean);
	}

	/**
	 * 批量新增
	 */
	@Override
	public Integer addBatch(List<NoticeReadInfo> listBean) {
		if(listBean == null || listBean.isEmpty()) {
			return 0;
		}
		return this.noticeReadInfoMapper.insertBatch(listBean);
	}

	/**
	 * 批量新增或修改
	 */
	@Override
	public Integer addOrUpdateBatch(List<NoticeReadInfo> listBean) {
		if(listBean == null || listBean.isEmpty()) {
			return 0;
		}
		return this.noticeReadInfoMapper.insertOrUpdateBatch(listBean);
	}

	/**
	 * 根据Id查询
	 */
	@Override
	public NoticeReadInfo getNoticeReadInfoById(Long id) {
		return this.noticeReadInfoMapper.selectById(id);
	}

	/**
	 * 根据Id更新
	 */
	@Override
	public Integer updateNoticeReadInfoById(NoticeReadInfo bean, Long id) {
		return this.noticeReadInfoMapper.updateById(bean, id);
	}

	/**
	 * 根据Id删除
	 */
	@Override
	public Integer deleteNoticeReadInfoById(Long id) {
		return this.noticeReadInfoMapper.deleteById(id);
	}

	/**
	 * 根据NoticeIdAndUsername查询
	 */
	@Override
	public NoticeReadInfo getNoticeReadInfoByNoticeIdAndUsername(Long noticeId, String username) {
		return this.noticeReadInfoMapper.selectByNoticeIdAndUsername(noticeId, username);
	}

	/**
	 * 根据NoticeIdAndUsername更新
	 */
	@Override
	public Integer updateNoticeReadInfoByNoticeIdAndUsername(NoticeReadInfo bean, Long noticeId, String username) {
		return this.noticeReadInfoMapper.updateByNoticeIdAndUsername(bean, noticeId, username);
	}

	/**
	 * 根据NoticeIdAndUsername删除
	 */
	@Override
	public Integer deleteNoticeReadInfoByNoticeIdAndUsername(Long noticeId, String username) {
		return this.noticeReadInfoMapper.deleteByNoticeIdAndUsername(noticeId, username);
	}


}