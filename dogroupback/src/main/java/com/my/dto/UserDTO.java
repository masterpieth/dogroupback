package com.my.dto;

/**
 * 회원 DTO
 * @author NYK
 *
 */
public class UserDTO {
	private String email;			//User 이메일(PK)
	private String name;			//User 이름
	private String password;		//User 비밀번호
	private int diligence;			//User 성실도
	private int userBalance;		//User 지갑 현재 총 잔액
	private int status;				//User 현재상태 1:활성회원 / 0:탈퇴회원
	
	public UserDTO(String email, String password , String name) {
		super();
		this.email= email;
		this.password= password;
		this.name = name;
		
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public int getDiligence() {
		return diligence;
	}

	public void setDiligence(int diligence) {
		this.diligence = diligence;
	}

	public int getUserBalance() {
		return userBalance;
	}

	public void setUserBalance(int userBalance) {
		this.userBalance = userBalance;
	}

	public int getStatus() {
		return status;
	}

	public void setStatus(int status) {
		this.status = status;
	}

	@Override
	public String toString() {
		return "UserDTO [email=" + email + ", name=" + name + ", password=" + password + ", diligence=" + diligence
				+ ", userBalance=" + userBalance + ", status=" + status + "]";
	}
	
}
