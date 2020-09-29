package com.esteel.common.dto;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * @version 1.0.0
 * @ClassName PaginationSupport.java
 * @author: liu Jie
 * @Description: 分页
 * @createTime: 2020年-05月-19日  13:29
 */
public class PaginationSupport<T> implements Serializable {

	public static <T, R> PaginationSupport<R> transform(PaginationSupport<T> from, Function<T, R> function) {
		List<R> list = new ArrayList<R>();
		if (from.getList() != null) {
			list = from.getList().stream().map(function).collect(Collectors.toList());
		}
		PaginationSupport<R> to = new PaginationSupport<R>(list, from.getTotalCount(), from.getPageSize(), from.getStartIndex());
		to.setCurrentIndex(from.getCurrentIndex());
        return to;
	}

	private static final long serialVersionUID = 1L;

	public static final PaginationSupport EMPTY_PAGINATION_SUPPORT = new EmptyPaginationSupport();

	public final static int PAGESIZE = 10;

	private int indexCountOnShow = 7;

	private int pageSize = PAGESIZE;

	private List<T> list;

	private int[] indexes = new int[0];

	private int startIndex = 0;

	// 下一页
	private int nextIndex;
	// 上一页
	private int previousIndex;
	// 总页数
	private int pageCount;
	// 当前页
	private int currentIndex;
	// 最后一页
	private int endIndex;
	// 总条数
	private int totalCount;

	/**
	 * @return Returns the endIndex.
	 */
	public int getEndIndex() {
		return endIndex;
	}

	/**
	 * @param endIndex
	 *            The endIndex to set.
	 */
	public void setEndIndex(int endIndex) {
		this.endIndex = endIndex;
	}

	/**
	 * @return Returns the currentIndex.
	 */
	public int getCurrentIndex() {
		return currentIndex;
	}

	/**
	 * @param currentIndex
	 *            The currentIndex to set.
	 */
	public void setCurrentIndex(int currentIndex) {
		this.currentIndex = currentIndex;
	}

	public PaginationSupport(List<T> list, int totalCount) {
		this(list, totalCount, PAGESIZE, 0);
	}

	public PaginationSupport(List<T> list, int totalCount, int startIndex) {
		this(list, totalCount, PAGESIZE, startIndex);
	}

	public PaginationSupport(List<T> list, int totalCount, int pageSize, int startIndex) {
		this.pageSize = pageSize;
		this.totalCount = totalCount;
		this.list = list;
		this.startIndex = startIndex;
		init();
	}

	public PaginationSupport(List<T> list, int totalCount, int pageSize, int startIndex, int indexCountOnShow) {
		setPageSize(pageSize);
		setTotalCount(totalCount);
		setList(list);
		setStartIndex(startIndex);
		this.setIndexCountOnShow(indexCountOnShow);

		init();
	}

	/**
	 * @return Returns the pageCount.
	 */
	public int getPageCount() {
		return pageCount;
	}

	/**
	 * @param pageCount
	 *            The pageCount to set.
	 */
	public void setPageCount(int pageCount) {
		this.pageCount = pageCount;
	}

	private void init() {

		if (totalCount > 0) {
			pageCount = totalCount / pageSize;
			if (totalCount % pageSize > 0){
				pageCount++;
			}
		} else {
			pageCount = 0;
		}

		currentIndex = startIndex / pageSize + 1;

		endIndex = pageCount;

		nextIndex = currentIndex >= endIndex ? endIndex : currentIndex + 1;
		previousIndex = currentIndex > 1 ? currentIndex - 1 : 1;
	}

	public List<T> getList() {
		return list;
	}

	public void setList(List<T> list) {
		this.list = list;
	}

	public int getPageSize() {
		return pageSize;
	}

	public void setPageSize(int pageSize) {
		this.pageSize = pageSize;
	}

	public int getTotalCount() {
		return totalCount;
	}

	public void setTotalCount(int totalCount) {
		this.totalCount = totalCount;
	}

	public int[] getIndexes() {
		return indexes;
	}

	public void setIndexes(int[] indexes) {
		this.indexes = indexes;
	}

	public int getStartIndex() {
		return startIndex;
	}

	public void setStartIndex(int startIndex) {
		this.startIndex = startIndex;
	}

	/**
	 * @return Returns the nextIndex.
	 */
	public int getNextIndex() {
		return nextIndex;
	}

	/**
	 * @param nextIndex
	 *            The nextIndex to set.
	 */
	public void setNextIndex(int nextIndex) {
		this.nextIndex = nextIndex;
	}

	/**
	 * @return Returns the previousIndex.
	 */
	public int getPreviousIndex() {
		return previousIndex;
	}

	/**
	 * @param previousIndex
	 *            The previousIndex to set.
	 */
	public void setPreviousIndex(int previousIndex) {
		this.previousIndex = previousIndex;
	}

	public int getIndexCountOnShow() {
		return indexCountOnShow;
	}

	public void setIndexCountOnShow(int indexCountOnShow) {
		this.indexCountOnShow = indexCountOnShow;
	}

	public int getFirst() {
		int result = (currentIndex - 1) * pageSize;
		return (result < 0 ? 0 : result);
	}

	public int getStartIndexOnShow() {
		if (currentIndex < (indexCountOnShow / 2 + 1)) {
			return 1;
		}
		else {
			if (currentIndex > endIndex - (indexCountOnShow / 2 - 1)){
				return (endIndex - (indexCountOnShow - 1) > 0) ? endIndex - (indexCountOnShow - 1) : 1;
			}
			else {return currentIndex - indexCountOnShow / 2;}
		}
	}

	public int getEndIndexOnShow() {
		if (currentIndex < (indexCountOnShow / 2 + 1)) {
			if (endIndex > indexCountOnShow){
				return indexCountOnShow;
			}
			else{
				return endIndex;
			}
		} else {
			if (currentIndex >= endIndex - (indexCountOnShow / 2 - 1)){
				return endIndex;
			}
			else {
				return currentIndex + indexCountOnShow / 2;
			}
		}
	}

	public int getPageNumber() {
		return startIndex / pageSize + 1;
	}

	public int getObjectsPerPage() {
		return pageSize;
	}

	public int getFullListSize() {
		return totalCount;
	}

	public String getSortCriterion() {
		return null;
	}

	public String getSearchId() {
		return null;
	}

	private static final class EmptyPaginationSupport<T> extends PaginationSupport<T> {

		/**
		 * 
		 */
		private static final long serialVersionUID = 1L;

		public EmptyPaginationSupport() {
			super(Collections.<T> emptyList(), 0);
		}

		@Override
		public void setList(List<T> list) {
			throw new UnsupportedOperationException();
		}
	}

}
