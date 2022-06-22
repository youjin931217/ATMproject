package main;
 
import java.sql.*;
import java.util.InputMismatchException;
import java.util.Scanner;
import java.util.regex.Pattern; 
 
 public class Main {
    
    //DB����
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

    //DB���� �޼���
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
    
    //���� ���� (���� ���� DB�Է�)
    public static void addAccount() {
    	String newAccountId;
    	String newName;
    	int newPw;
    	long newBalance;
    	int result = 0;    	
    	
    	while(true) {    		
    		try {
    			a: while(true) {
    			 System.out.print("���¹�ȣ �Է�(���� 10�ڸ�) : "); newAccountId = scan.next();
    				boolean go = Pattern.matches(matchId, newAccountId);
    				
    				if(go) {
    					String sql = "select * from account ";
    					try {
    						psmt = con.prepareStatement(sql);
    			            rs = psmt.executeQuery();
    			            while(rs.next()) {
    			            	if(newAccountId.equals(rs.getString(1))) {
    			            		System.out.println("[�ߺ��� ���¹�ȣ �Դϴ�.]");
    			            		continue a;
    			            		}    			            	
    			            	}
    			            }catch(Exception e) {    
    			            	e.printStackTrace();
    			            	}
    					break;
    					} else {
    					System.out.println("[�´� ���¹�ȣ ������ �Է����ּ���.]");
    				}
    			}
    				System.out.print("��й�ȣ �Է� : "); newPw = scan.nextInt();
    				System.out.print("�̸� �Է� : "); newName = scan.next();
    				System.out.print("���� �Աݾ� : "); newBalance = scan.nextLong();
    				break;   				
    				
			} catch (InputMismatchException e) {
				scan = new Scanner(System.in);
				System.out.println("[������ ������ϴ�.]\n");
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
        System.out.println("[���� ���� ���� �Ϸ�]\n ");
        }
    
    //�Ա�
    public static void deposit() {
    	String sql = null;
        long depositMoney;        
        
        while(true) {
            try {
            	while(true) {
            		System.out.print("���¹�ȣ �Է� : "); accountId = scan.next(); 
                    boolean result = Pattern.matches(matchId, accountId);
                    
                    if(result) {
                 	   break;
                 	   }else {
                 		   System.out.println("[�´� ���¹�ȣ ������ �Է����ּ���.]\n");
                 		   }
                    }
            	System.out.print("�Աݾ� : "); depositMoney = scan.nextLong();
                break;
               } catch (InputMismatchException e) {
            	   scan = new Scanner(System.in);
            	   System.out.println("[�߸� �Է��ϼ̽��ϴ�.]\n");
            	   }
            }
        
        try {	
        //��ȸ�ϱ�
        sql = "select balance from account where accountid=?";
        psmt = con.prepareStatement(sql);
        psmt.setString(1, accountId);
        rs = psmt.executeQuery();
        rs.next();
        balance = rs.getLong(1);
        
        //�����ϱ�
        sql = "update account set balance=? where accountid=?";
        psmt = con.prepareStatement(sql);
        psmt.setLong (1, balance + depositMoney);
        psmt.setString(2, accountId);
        psmt.execute();
        } catch(Exception e) {
        	e.printStackTrace();
        	}
        System.out.println("�Ա��� �Ϸ�Ǿ����ϴ�.");
        }
 
    //���� ��Ī
    public static boolean login() {
    	System.out.print("\n���¹�ȣ �Է� : "); accountId = scan.next();
    	System.out.print("��й�ȣ �Է�(���� 4�ڸ�) : "); pw = scan.nextInt();  
         
    	boolean result = true;
    	String sql = "select * from account ";
    	try {
    		psmt = con.prepareStatement(sql);
            rs = psmt.executeQuery();
            while(rs.next()) {
            	if(accountId.equals(rs.getString(1)) && pw != rs.getInt(2)) {
            		System.out.println("��й�ȣ�� ��ġ���� �ʽ��ϴ�.");
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
 
        //���
        public static void withdraw() {
        String sql = null;
        long withdrawMoney;
        
        System.out.print("��ݾ� : "); withdrawMoney = scan.nextLong();
        
        try {
    
        //��ȸ�ϱ�
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
        System.out.println("����� �Ϸ�Ǿ����ϴ�.");
        }else {
 
        System.out.println("�ܾ��� ������� �ʽ��ϴ�.");
 
        }
        }catch (Exception e) {
        e.printStackTrace();
        }
        }
        
        //��ü
        public static void transfer() {
        	String target;
        	long transfer;
        	
        	System.out.print("�۱ݹ��� ���¹�ȣ : "); target = scan.next();
            System.out.print("�۱ݾ� : "); transfer = scan.nextLong();
            
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
 
            		//�ܾ� �߰�
            		sql = "update account set balance=? where accountId=?";
            		psmt = con.prepareStatement(sql);
            		psmt.setLong(1, targetBalance+transfer);
            		psmt.setString(2, target);
            		psmt.execute();
 
            		//�ܾ� ����
            		sql = "update account set balance=? where accountId=?";
            		psmt = con.prepareStatement(sql);
            		psmt.setLong(1, balance-transfer);
            		psmt.setString(2, accountId);
            		psmt.execute();
            		}else {
            			System.out.println("�ܾ��� �����մϴ�.");
            			}
            	}catch (Exception e) {
            		e.printStackTrace();
            		}
            }
 
        //���� �ݾ� ��ȸ
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
        System.out.println("[" + name + "�� �ܾ��� " + balance +"�� �Դϴ�.]\n");
        }

        
        public static void main(String[] args) {
        	int select;	
        	
        		while(true) {
        			while(true) {
        				try {
        					System.out.println("<< ATM >>");
        					System.out.println("1.���°���   2.�Ա�   3.���   4.��ü   5.�ܾ���ȸ   6.���α׷� ����");
        					System.out.print(">> ");        		     	
           		     		select = scan.nextInt();
           		     		System.out.println();
           		     		break;
           		     		} catch(InputMismatchException e) {
           		     			scan = new Scanner(System.in);
           		     			System.out.println("[�ùٸ� ��ȣ�� �Է��ϼ���.]\n");
           		     			}       			 
        				}       
 
        switch(select) { 
       
        case 1://���� �߰�
        addAccount();
        break;
 
        case 2://�Ա�
        deposit();
        break;
 
        case 3://���
        if(login()) {
        	withdraw();
        	}
        break;
 
        case 4://�۱�
        if(login()) {
        	transfer();
        }
        break;
 
        case 5://�ܾ���ȸ
        if(login()) {
        checkBalance();
        }
        break;

        case 6://����
        	System.out.println("[���α׷��� �����մϴ�.]");
        	System.exit(0);
        	break;
        
        default ://��ȣ ��������
        	System.out.println("[�ùٸ� ��ȣ�� �Է��ϼ���.]\n");
                }        
            }
        }
    }