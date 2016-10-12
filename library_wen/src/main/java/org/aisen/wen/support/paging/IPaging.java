package org.aisen.wen.support.paging;

import java.io.Serializable;

public interface IPaging<Item extends Serializable, Result extends Serializable> extends Serializable {

	/**
	 * 处理数据
	 * 
	 * @param newDatas
	 *            新获取的数据集合
	 * @param firstData
	 *            adapter数据集中的第一条数据
	 * @param lastData
	 *            adapter数据集中的最后一条数据
	 */
    void processData(Result newDatas, Item firstData, Item lastData);

    String getPreviousPage();

    String getNextPage();

}
