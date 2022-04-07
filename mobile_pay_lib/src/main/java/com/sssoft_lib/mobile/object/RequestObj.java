package com.sssoft_lib.mobile.object;

public class RequestObj {
	private String txnType;
	private String payMode;
	private String merchantTxnNo;
	private String merchantOrderNo;
	private String txnAmt;
	private String currencyCode;
	private String txnReqTime;
	private String permitDisctAmt;
	private String cashierID;
	private String txnLongDesc;
	private String txnShortDesc;
	private String itemDetail;
	private String refundTxnNo;
	private String refundAmt;
	private String orgMerchantID;
	private String orgMultData;
	private String refundReason;
	private String channelID;
	private String printFlag;
	private String queryType;
	private String smType;
	private String orgPlatformTxnNo;
	private String orgTxnNo;

	//20190403 预授权类交易
	private String addInfo;
	private String authNo;
	private String opType;
	
	
	public String getOrgTxnNo() {
		return orgTxnNo;
	}
	public void setOrgTxnNo(String orgTxnNo) {
		this.orgTxnNo = orgTxnNo;
	}
	public String getOrgPlatformTxnNo() {
		return orgPlatformTxnNo;
	}
	public void setOrgPlatformTxnNo(String orgPlatformTxnNo) {
		this.orgPlatformTxnNo = orgPlatformTxnNo;
	}
	public String getQueryType() {
		return queryType;
	}
	public void setQueryType(String queryType) {
		this.queryType = queryType;
	}
	public String getSmType() {
		return smType;
	}
	public void setSmType(String smType) {
		this.smType = smType;
	}
	public String getPrintFlag() {
		return printFlag;
	}
	public void setPrintFlag(String printFlag) {
		this.printFlag = printFlag;
	}
	public String getChannelID() {
		return channelID;
	}
	public void setChannelID(String channelID) {
		this.channelID = channelID;
	}
	public String getRefundTxnNo() {
		return refundTxnNo;
	}
	public void setRefundTxnNo(String refundTxnNo) {
		this.refundTxnNo = refundTxnNo;
	}
	public String getRefundAmt() {
		return refundAmt;
	}
	public void setRefundAmt(String refundAmt) {
		this.refundAmt = refundAmt;
	}
	public String getOrgMerchantID() {
		return orgMerchantID;
	}
	public void setOrgMerchantID(String orgMerchantID) {
		this.orgMerchantID = orgMerchantID;
	}
	public String getOrgMultData() {
		return orgMultData;
	}
	public void setOrgMultData(String orgMultData) {
		this.orgMultData = orgMultData;
	}
	public String getRefundReason() {
		return refundReason;
	}
	public void setRefundReason(String refundReason) {
		this.refundReason = refundReason;
	}
	public String getTxnType() {
		return txnType;
	}
	public void setTxnType(String txnType) {
		this.txnType = txnType;
	}
	public String getPayMode() {
		return payMode;
	}
	public void setPayMode(String payMode) {
		this.payMode = payMode;
	}
	public String getMerchantTxnNo() {
		return merchantTxnNo;
	}
	public void setMerchantTxnNo(String merchantTxnNo) {
		this.merchantTxnNo = merchantTxnNo;
	}
	public String getTxnAmt() {
		return txnAmt;
	}
	public void setTxnAmt(String txnAmt) {
		this.txnAmt = txnAmt;
	}
	public String getCurrencyCode() {
		return currencyCode;
	}
	public void setCurrencyCode(String currencyCode) {
		this.currencyCode = currencyCode;
	}
	public String getTxnReqTime() {
		return txnReqTime;
	}
	public void setTxnReqTime(String txnReqTime) {
		this.txnReqTime = txnReqTime;
	}
	public String getPermitDisctAmt() {
		return permitDisctAmt;
	}
	public void setPermitDisctAmt(String permitDisctAmt) {
		this.permitDisctAmt = permitDisctAmt;
	}
	public String getCashierID() {
		return cashierID;
	}
	public void setCashierID(String cashierID) {
		this.cashierID = cashierID;
	}
	public String getTxnLongDesc() {
		return txnLongDesc;
	}
	public void setTxnLongDesc(String txnLongDesc) {
		this.txnLongDesc = txnLongDesc;
	}
	public String getTxnShortDesc() {
		return txnShortDesc;
	}
	public void setTxnShortDesc(String txnShortDesc) {
		this.txnShortDesc = txnShortDesc;
	}
	public String getItemDetail() {
		return itemDetail;
	}
	public void setItemDetail(String itemDetail) {
		this.itemDetail = itemDetail;
	}


	public String getMerchantOrderNo() {
		return merchantOrderNo;
	}

	public void setMerchantOrderNo(String merchantOrderNo) {
		this.merchantOrderNo = merchantOrderNo;
	}

	public String getAuthNo() {
		return authNo;
	}

	public void setAuthNo(String authNo) {
		this.authNo = authNo;
	}

	public String getOpType() {
		return opType;
	}

	public void setOpType(String opType) {
		this.opType = opType;
	}

	public String getAddInfo() {
		return addInfo;
	}

	public void setAddInfo(String addInfo) {
		this.addInfo = addInfo;
	}
}
