/**
 * Copyright (C) 2017 LG Electronics Inc. All Rights Reserved.
 * Though every care has been taken to ensure the accuracy of this document,
 * LG Electronics Inc. cannot accept responsibility for any errors or
 * omissions or for any loss occurred to any person, whether legal or natural,
 * from acting, or refraining from action, as a result of the information
 * contained herein. Information in this document is subject to change at any
 * time without obligation to notify any person of such changes.
 * LG Electronics Inc. may have patents or patent pending applications,
 * trademarks copyrights or other intellectual property rights covering subject
 * matter in this document. The furnishing of this document does not give the
 * recipient or reader any license to these patents, trademarks copyrights or
 * other intellectual property rights.
 * No part of this document may be communicated, distributed, reproduced or
 * transmitted in any form or by any means, electronic or mechanical or
 * otherwise, for any purpose, without the prior written permission of
 * LG Electronics Inc.
 * The document is subject to revision without further notice.
 * All brand names and product names mentioned in this document are trademarks
 * or registered trademarks of their respective owners
 *
 * Author: eungsuk.shon
 */

package com.ses.coin.coin_core.util;

public class ArrayQueue {
  
  // 큐 배열은 front와 rear 그리고 maxSize를 가진다.
  private int front;
  private int rear;
  private int maxSize;
  private Object[] queueArray;
  
  // 큐 배열 생성
  public ArrayQueue(int maxSize){
      
      this.front = 0;
      this.rear = -1;
      this.maxSize = maxSize;
      this.queueArray = new Object[maxSize];
  }
  
  // 큐가 비어있는지 확인
  public boolean empty(){
      return (front == rear+1);
  }
  
  // 큐가 꽉 찼는지 확인
  public boolean full(){
      return (rear == maxSize-1);
  }
  
  // 큐 rear에 데이터 등록
  public void insert(Object item){
      
      if(full()) throw new ArrayIndexOutOfBoundsException();
      
      queueArray[++rear] = item;
  }
  
  // 큐에서 front 데이터 조회
  public Object peek(){
      
      if(empty()) throw new ArrayIndexOutOfBoundsException();
      
      return queueArray[front];
  }
  
  // 큐에서 front 데이터 제거
  public Object remove(){
      
      Object item = peek();
      front++;
      return item;
  }

}
