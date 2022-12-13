package com.my.dto;

import java.sql.Date;

/**
 * 지갑 DTO
 * @author NYK
 *
 */
public class WalletDTO {
	private int transactionNo;			//거래번호(PK)
	private String email;				//User 이메일
	private int walletBalance;			//User 현재 총잔액
	private Date transcationDate;		//거래일자
	private int studyId;				//스터디 ID(스터디 관련 거래일경우 - 아닐 경우에는 NULL
	private String transactionUser;		//거래자(통장입출금 관련 거래일 경우 - 아닐 경우에는 NULL
	private int transactionCategory;	//거래유형(1 : 정산금액(환급금)/2: 입장료/3: 충전금/4: 인출금
	private int transactionMoney;		//거래금액
	
	public WalletDTO() {
		super();
	}
	
	public WalletDTO(int transactionNo, String email, int walletBalance, Date transcationDate, int studyId,
			String transactionUser, int transactionCategory, int transactionMoney) {
		super();
		this.transactionNo = transactionNo;
		this.email = email;
		this.walletBalance = walletBalance;
		this.transcationDate = transcationDate;
		this.studyId = studyId;
		this.transactionUser = transactionUser;
		this.transactionCategory = transactionCategory;
		this.transactionMoney = transactionMoney;
	}

	public int getTransactionNo() {
		return transactionNo;
	}

	public void setTransactionNo(int transactionNo) {
		this.transactionNo = transactionNo;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public int getWalletBalance() {
		return walletBalance;
	}

	public void setWalletBalance(int walletBalance) {
		this.walletBalance = walletBalance;
	}

	public Date getTranscationDate() {
		return transcationDate;
	}

	public void setTranscationDate(Date transcationDate) {
		this.transcationDate = transcationDate;
	}

	public int getStudyId() {
		return studyId;
	}

	public void setStudyId(int studyId) {
		this.studyId = studyId;
	}

	public String getTransactionUser() {
		return transactionUser;
	}

	public void setTransactionUser(String transactionUser) {
		this.transactionUser = transactionUser;
	}

	public int getTransactionCategory() {
		return transactionCategory;
	}

	public void setTransactionCategory(int transactionCategory) {
		this.transactionCategory = transactionCategory;
	}

	public int getTransactionMoney() {
		return transactionMoney;
	}

	public void setTransactionMoney(int transactionMoney) {
		this.transactionMoney = transactionMoney;
	}

	@Override
	public String toString() {
		return "WalletDTO [transactionNo=" + transactionNo + ", email=" + email + ", walletBalance=" + walletBalance
				+ ", transcationDate=" + transcationDate + ", studyId=" + studyId + ", transactionUser="
				+ transactionUser + ", transactionCategory=" + transactionCategory + ", transactionMoney="
				+ transactionMoney + "]";
	}
}
