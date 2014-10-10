package com.m.support.paging;

import java.io.Serializable;

/**
 * 分页代理，根据每次调用接口返回的数据设置分页参数<br/>
 * {@link #processData}处理后的数据保存，在下次调用{@link #generateHttpParams}时，返回当前分页的参数
 * 
 * @author wangdan
 * 
 * @param <T>
 * @param <Ts>
 */
public class PagingProxy<T extends Serializable, Ts extends Serializable> implements IPaging<T, Ts> {

	private static final long serialVersionUID = -1986413629779663810L;
	private IPaging<T, Ts> pagingProcessor;

	public PagingProxy(IPaging<T, Ts> pagingProcessor) {
		this.pagingProcessor = pagingProcessor;
	}

	@Override
	public IPaging<T, Ts> newInstance() {
		pagingProcessor = pagingProcessor.newInstance();
		return null;
	}

	@Override
	public void processData(Ts newDatas, T firstData, T lastData) {
		pagingProcessor.processData(newDatas, firstData, lastData);
	}

	@Override
	public boolean canRefresh() {
		return pagingProcessor.canRefresh();
	}

	@Override
	public boolean canUpdate() {
		return pagingProcessor.canUpdate();
	}

	public IPaging<T, Ts> getPagingProcessor() {
		return pagingProcessor;
	}

	public void setPagingProcessor(IPaging<T, Ts> pagingProcessor) {
		this.pagingProcessor = pagingProcessor;
	}

	@Override
	public String getPreviousPage() {
		return pagingProcessor.getPreviousPage();
	}

	@Override
	public String getNextPage() {
		return pagingProcessor.getNextPage();
	}

	@Override
	public void setPage(String previousPage, String nextPage) {
		pagingProcessor.setPage(previousPage, nextPage);
	}

}
