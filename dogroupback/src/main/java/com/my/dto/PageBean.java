package com.my.dto;

import java.util.List;

public class PageBean<T> {
	private int currentPage;// 현재페이지
	public static final int CNT_PER_PAGE = 5; // 페이지당 보여줄 상품수
	private List<T> list; // 페이지에 해당하는 목록
	private int totalCnt; // 총목록수
	private int totalPage; // 총페이지수
	private int cntPerPageGroup = 5; // 페이지목록 그룹수
	private int startPage; // 페이지그룹의 시작페이지
	private int endPage; // 페이지그룹의 끝페이지

	public PageBean(int currentPage, List<T> list, int totalCnt) {
		this.currentPage = currentPage;
		this.list = list;
		this.totalCnt = totalCnt;
		this.totalPage = (int)(Math.ceil((double)totalCnt/CNT_PER_PAGE));
		
		if(currentPage <= cntPerPageGroup/2) {
			startPage = 1;
			endPage = cntPerPageGroup;
		}
		else if(currentPage > (totalPage - cntPerPageGroup/2)) {
			if(currentPage == totalPage) startPage = totalPage - cntPerPageGroup + 1;
			else startPage = totalPage - cntPerPageGroup;
			endPage = totalPage;
		}
		else {
			startPage = currentPage - cntPerPageGroup/2;
			endPage = currentPage + cntPerPageGroup/2;
		}
	}

	public int getCurrentPage() {
		return currentPage;
	}

	public void setCurrentPage(int currentPage) {
		this.currentPage = currentPage;
	}

	public List<T> getList() {
		return list;
	}

	public void setList(List<T> list) {
		this.list = list;
	}

	public int getTotalCnt() {
		return totalCnt;
	}

	public void setTotalCnt(int totalCnt) {
		this.totalCnt = totalCnt;
	}

	public int getTotalPage() {
		return totalPage;
	}

	public void setTotalPage(int totalPage) {
		this.totalPage = totalPage;
	}

	public int getCntPerPageGroup() {
		return cntPerPageGroup;
	}

	public void setCntPerPageGroup(int cntPerPageGroup) {
		this.cntPerPageGroup = cntPerPageGroup;
	}

	public int getStartPage() {
		return startPage;
	}

	public void setStartPage(int startPage) {
		this.startPage = startPage;
	}

	public int getEndPage() {
		return endPage;
	}

	public void setEndPage(int endPage) {
		this.endPage = endPage;
	}

	public static int getCntPerPage() {
		return CNT_PER_PAGE;
	}

}
