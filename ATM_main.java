package main;
 
import java.sql.*;
import java.util.InputMismatchException;
import java.util.Scanner;
import java.util.regex.Pattern; 
 
 public class Main {
    
    //DB연결
    static String url = "jdbc:oracle:thin:@localhost:1521:xe";
    static String dbid = "hr";
    static String dbpw = "hr";
    static Scanner scan = new Scanner(System.in);
    
    static ResultSet rs = null;
    static PreparedStatement psmt = null;
    static Connection con = getConnectivity();
    
    static String accountId;
    static int pw;
    static long balance;
    static String matchId = "\\d{4}-\\d{4}-\\d{4}-\\d{2}";

    //DB연결 메서드
    public static Connection getConnectivity() {
        Connection con = null;
        try {
            Class.forName("oracle.jdbc.driver.OracleDriver");
            con = DriverManager.getConnection(url, dbid, dbpw);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return con;
    }   
    
    //계좌 개설 (계좌 정보 DB입력)
    public static void addAccount() {
    	String newAccountId;
    	String newName;
    	int newPw;
    	long newBalance;
    	int result = 0;    	
    	
    	while(true) {    		
    		try {
    			a: while(true) {
    			 System.out.print("계좌번호 입력(숫자 10자리) : "); newAccountId = scan.next();
    				boolean go = Pattern.matches(matchId, newAccountId);
    				
    				if(go) {
    					String sql = "select * from account ";
    					try {
    						psmt = con.prepareStatement(sql);
    			            rs = psmt.executeQuery();
    			            while(rs.next()) {
    			            	if(newAccountId.equals(rs.getString(1))) {
    			            		System.out.println("[중복된 계좌번호 입니다.]");
    			            		continue a;
    			            		}    			            	
    			            	}
    			            }catch(Exception e) {    
    			            	e.printStackTrace();
    			            	}
    					break;
    					} else {
    					System.out.println("[맞는 계좌번호 형식을 입력해주세요.]");
    				}
    			}
    				System.out.print("비밀번호 입력 : "); newPw = scan.nextInt();
    				System.out.print("이름 입력 : "); newName = scan.next();
    				System.out.print("최초 입금액 : "); newBalance = scan.nextLong();
    				break;   				
    				
			} catch (InputMismatchException e) {
				scan = new Scanner(System.in);
				System.out.println("[범위를 벗어났습니다.]\n");
			} 
    	}
    	
    	Account ac = new Account(newAccountId, newPw, newName, newBalance);
    	String sql = "insert into account values(?,?,?,?)";

        try {
            psmt = con.prepareStatement(sql);
            psmt.setString(1, ac.getAccountId());
            psmt.setInt(2, ac.getPw());
            psmt.setString(3, ac.getName());
            psmt.setLong(4, ac.getBalance());
            result = psmt.executeUpdate();
            } catch(Exception e) {
            	e.printStackTrace();
            	}
        System.out.println("[계좌 정보 저장 완료]\n ");
        }
    
    //입금
    public static void deposit() {
    	String sql = null;
        long depositMoney;        
        
        while(true) {
            try {
            	while(true) {
            		System.out.print("계좌번호 입력 : "); accountId = scan.next(); 
                    boolean result = Pattern.matches(matchId, accountId);
                    
                    if(result) {
                 	   break;
                 	   }else {
                 		   System.out.println("[맞는 계좌번호 형식을 입력해주세요.]\n");
                 		   }
                    }
            	System.out.print("입금액 : "); depositMoney = scan.nextLong();
                break;
               } catch (InputMismatchException e) {
            	   scan = new Scanner(System.in);
            	   System.out.println("[잘못 입력하셨습니다.]\n");
            	   }
            }
        
        try {	
        //조회하기
        sql = "select balance from account where accountid=?";
        psmt = con.prepareStatement(sql);
        psmt.setString(1, accountId);
        rs = psmt.executeQuery();
        rs.next();
        balance = rs.getLong(1);
        
        //수정하기
        sql = "update account set balance=? where accountid=?";
        psmt = con.prepareStatement(sql);
        psmt.setLong (1, balance + depositMoney);
        psmt.setString(2, accountId);
        psmt.execute();
        } catch(Exception e) {
        	e.printStackTrace();
        	}
        System.out.println("입금이 완료되었습니다.");
        }
 
    //계좌 매칭
    public static boolean login() {
    	System.out.print("\n계좌번호 입력 : "); accountId = scan.next();
    	System.out.print("비밀번호 입력(숫자 4자리) : "); pw = scan.nextInt();  
         
    	boolean result = true;
    	String sql = "select * from account ";
    	try {
    		psmt = con.prepareStatement(sql);
            rs = psmt.executeQuery();
            while(rs.next()) {
            	if(accountId.equals(rs.getString(1)) && pw != rs.getInt(2)) {
            		System.out.println("비밀번호가 일치하지 않습니다.");
            		result = false;
            		}else if(accountId.equals(rs.getString(1)) && pw == rs.getInt(2)) {
            			result = true;
            			}    
            		}
            	}catch(Exception e) {    
            		e.printStackTrace();
            		}
            return result;
            }
 
        //출금
        public static void withdraw() {
        String sql = null;
        long withdrawMoney;
        
        System.out.print("출금액 : "); withdrawMoney = scan.nextLong();
        
        try {
    
        //조회하기
        sql = "select balance from account where accountId=?";
        psmt = con.prepareStatement(sql);
        psmt.setString(1, accountId);
        rs = psmt.executeQuery();
        rs.next();
        balance = rs.getLong(1);
 
        if(balance >= withdrawMoney) {
        sql = "update account set balance=? where accountId=?";
        psmt = con.prepareStatement(sql);
        psmt.setLong(1, balance-withdrawMoney);
        psmt.setString(2, accountId);
        psmt.execute();
        System.out.println("출금이 완료되었습니다.");
        }else {
 
        System.out.println("잔액이 충분하지 않습니다.");
 
        }
        }catch (Exception e) {
        e.printStackTrace();
        }
        }
        
        //이체
        public static void transfer() {
        	String target;
        	long transfer;
        	
        	System.out.print("송금받을 계좌번호 : "); target = scan.next();
            System.out.print("송금액 : "); transfer = scan.nextLong();
            
            String sql = null;
            long targetBalance;
            try {
            	sql = "select balance from account where accountId=?";
            	psmt = con.prepareStatement(sql);
            	psmt.setString(1, accountId);
            	rs = psmt.executeQuery();
            	rs.next();
            	balance = rs.getInt(1);
 
            	if(balance >= transfer) {
            		sql = "select balance from account where accountId=?";
            		psmt = con.prepareStatement(sql);
            		psmt.setString(1, target);
            		rs = psmt.executeQuery();
            		rs.next();
            		targetBalance = rs.getInt(1);
 
            		//잔액 추가
            		sql = "update account set balance=? where accountId=?";
            		psmt = con.prepareStatement(sql);
            		psmt.setLong(1, targetBalance+transfer);
            		psmt.setString(2, target);
            		psmt.execute();
 
            		//잔액 빠짐
            		sql = "update account set balance=? where accountId=?";
            		psmt = con.prepareStatement(sql);
            		psmt.setLong(1, balance-transfer);
            		psmt.setString(2, accountId);
            		psmt.execute();
            		}else {
            			System.out.println("잔액이 부족합니다.");
            			}
            	}catch (Exception e) {
            		e.printStackTrace();
            		}
            }
 
        //계좌 금액 조회
        public static void checkBalance() {
        String sql = null;
        String name = null;
        
        try {
        sql = "select balance, name from account where accountId=?";
        psmt = con.prepareStatement(sql);
        psmt.setString(1, accountId);
        rs = psmt.executeQuery();
        rs.next();
        balance = rs.getLong(1);
        name = rs.getString(2);
        } catch (Exception e) {
        e.printStackTrace();
        }
        System.out.println("[" + name + "의 잔액은 " + balance +"원 입니다.]\n");
        }

        
        public static void main(String[] args) {
        	int select;	
        	
        		while(true) {
        			while(true) {
        				try {
        					System.out.println("<< ATM >>");
        					System.out.println("1.계좌개설   2.입금   3.출금   4.이체   5.잔액조회   6.프로그램 종료");
        					System.out.print(">> ");        		     	
           		     		select = scan.nextInt();
           		     		System.out.println();
           		     		break;
           		     		} catch(InputMismatchException e) {
           		     			scan = new Scanner(System.in);
           		     			System.out.println("[올바른 번호를 입력하세요.]\n");
           		     			}       			 
        				}       
 
        switch(select) { 
       
        case 1://계좌 추가
        addAccount();
        break;
 
        case 2://입금
        deposit();
        break;
 
        case 3://출금
        if(login()) {
        	withdraw();
        	}
        break;
 
        case 4://송금
        if(login()) {
        	transfer();
        }
        break;
 
        case 5://잔액조회
        if(login()) {
        checkBalance();
        }
        break;

        case 6://종료
        	System.out.println("[프로그램을 종료합니다.]");
        	System.exit(0);
        	break;
        
        default ://번호 오류차단
        	System.out.println("[올바른 번호를 입력하세요.]\n");
                }        
            }
        }
    }