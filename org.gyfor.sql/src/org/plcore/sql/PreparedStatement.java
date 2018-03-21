/*******************************************************************************
 * Copyright (c) 2008 Kevin Holloway (kholloway@geckosoftware.com.au).
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Public License v3.0
 * which accompanies this distribution, and is available at
 * http://www.gnu.org/licenses/gpl.txt
 * 
 * Contributors:
 *     Kevin Holloway - initial API and implementation
 ******************************************************************************/
package org.plcore.sql;


import java.math.BigDecimal;
import java.math.BigInteger;
import java.net.URL;
import java.sql.Blob;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.time.LocalDate;
import java.util.Calendar;
import java.util.TimeZone;

import org.plcore.math.Decimal;


public class PreparedStatement implements IPreparedStatement {
  
  static final Calendar tzCal = Calendar.getInstance(TimeZone.getTimeZone("GMT"));

	private final String sql;
  protected final java.sql.PreparedStatement stmt;

  private int index = 0;
  

  PreparedStatement (String sql, java.sql.PreparedStatement stmt) {
    this.sql = sql;
    //System.out.println(".... SQL " + sql);
		this.stmt = stmt;
	}


  public String getSql () {
  	return sql;
  }
  
   
  @Override
  public void close () {
    try {
      stmt.close();
    } catch (SQLException ex) {
      throw new RuntimeException(ex);
    }
  }
  
  
  @Override
  public Blob createBlob () {
    try {
      return stmt.getConnection().createBlob();
    } catch (SQLException ex) {
      throw new RuntimeException(ex);
    }
  }

  
  @Override
  public void addBatch () {
    try {
      stmt.addBatch();
    } catch (SQLException ex) {
      throw new RuntimeException(ex);
    }
  }
  
  
  @Override
  public void executeBatch () {
    try {
      stmt.executeBatch();
    } catch (SQLException ex) {
      throw new RuntimeException(ex);
    }
  }
  
  
  @Override
  public void clearParameters () {
    try {
      stmt.clearParameters();
      index = 0;
    } catch (SQLException ex) {
      throw new RuntimeException(ex);
    }
  }
  

  //@Override
  private void setBigDecimal (int i, BigDecimal d) {
    try {
      stmt.setBigDecimal(i, d);
    } catch (SQLException ex) {
      throw new RuntimeException(ex);
    }
  }
  

  @Override
  public void setBigDecimal (BigDecimal d) {
    setBigDecimal (++index, d);
  }
  

  //@Override
  private void setBoolean (int i, boolean b) {
    try {
      stmt.setBoolean(i, b);
    } catch (SQLException ex) {
      throw new RuntimeException(ex);
    }
  }
  

  @Override
  public void setBoolean (boolean b) {
    setBoolean(++index, b);
  }
  

  //@Override
  private void setBlob (int i, Blob b) {
    try {
      stmt.setBlob(i, b);
    } catch (SQLException ex) {
      throw new RuntimeException(ex);
    }
  }
  

  @Override
  public void setBlob (Blob b) {
    setBlob(++index, b);
  }
  

  //@Override
  private void setBigInteger (int i, BigInteger d) {
    try {
      stmt.setBigDecimal(i, new BigDecimal(d));
    } catch (SQLException ex) {
      throw new RuntimeException(ex);
    }
  }
  

  @Override
  public void setBigInteger (BigInteger d) {
    setBigInteger (++index, d);
  }
  

  //@Override
  private void setBytes (int i, byte[] bx) {
    try {
      stmt.setBytes(i, bx);
    } catch (SQLException ex) {
      throw new RuntimeException(ex);
    }
  }
  

  @Override
  public void setBytes (byte[] bx) {
    setBytes (++index, bx);
  }
  

  //@Override
  private void setNull (int i, int sqlType) {
    try {
      stmt.setNull(i, sqlType);
    } catch (SQLException ex) {
      throw new RuntimeException(ex);
    }
  }
  
  
  @Override
  public void setNull (int sqlType) {
    setNull (++index, sqlType);
  }
  
  
  //@Override
  private void setString (int i, String s) {
    try {
      stmt.setString(i, s);
    } catch (SQLException ex) {
      throw new RuntimeException(ex);
    }
  }
  

  @Override
  public void setString (String s) {
    setString (++index, s);
  }
  

  //@Override
  private void setInt (int i, int v) {
    try {
      stmt.setInt(i, v);
    } catch (SQLException ex) {
      throw new RuntimeException(ex);
    }
  }


  @Override
  public void setFloat (float f) {
    setFloat (++index, f);
  }


  //@Override
  private void setFloat (int i, float f) {
    try {
      stmt.setFloat(i, f);
    } catch (SQLException ex) {
      throw new RuntimeException(ex);
    }
  }


  @Override
  public void setDouble (double d) {
    setDouble (++index, d);
  }


  //@Override
  private void setDouble (int i, double d) {
    try {
      stmt.setDouble(i, d);
    } catch (SQLException ex) {
      throw new RuntimeException(ex);
    }
  }


  @Override
  public void setInt (int v) {
    setInt (++index, v);
  }


  //@Override
  private void setShort (int i, short v) {
    try {
      stmt.setShort(i, v);
    } catch (SQLException ex) {
      throw new RuntimeException(ex);
    }
  }


  @Override
  public void setShort (short v) {
    setShort (++index, v);
  }


  //@Override
  private void setLong (int i, long v) {
    try {
      stmt.setLong(i, v);
    } catch (SQLException ex) {
      throw new RuntimeException(ex);
    }
  }


  @Override
  public void setLong (long v) {
    setLong (++index, v);
  }


  //@Override
  private void setDate (int i, java.sql.Date d) {
    try {
      stmt.setDate(i, new java.sql.Date(d.getTime()), tzCal);
    } catch (SQLException ex) {
      throw new RuntimeException(ex);
    }
  }


  @Override
  public void setDate (java.sql.Date d) {
    setDate (++index, d);
  }


  //@Override
  private void setLocalDate (int i, LocalDate d) {
    try {
      stmt.setDate(i, new java.sql.Date(d.toEpochDay()), tzCal);
    } catch (SQLException ex) {
      throw new RuntimeException(ex);
    }
  }


  @Override
  public void setLocalDate (LocalDate d) {
    setLocalDate (++index, d);
  }


  //@Override
  private void setTimestamp (int i, Timestamp ts) {
    try {
      stmt.setTimestamp(i, ts);
    } catch (SQLException ex) {
      throw new RuntimeException(ex);
    }
  }


  @Override
  public void setTimestamp (Timestamp ts) {
    setTimestamp(++index, ts);
  }


  //@Override
  private void setDecimal (int i, Decimal d) {
    try {
      stmt.setBigDecimal(i, new BigDecimal(d.toString()));
    } catch (SQLException ex) {
      throw new RuntimeException(ex);
    }
  }


  @Override
  public void setDecimal (Decimal d) {
    setDecimal(++index, d);
  }


  //@Override
  private void setEnum (int i, Enum<?> v) {
    try {
      stmt.setInt(i, v.ordinal());
    } catch (SQLException ex) {
      throw new RuntimeException(ex);
    }
  }


  @Override
  public void setEnum (Enum<?> v) {
    setEnum(++index, v);
  }


  //@Override
  private void setURL (int i, URL v) {
    try {
      stmt.setString(i, v.toString());
    } catch (SQLException ex) {
      throw new RuntimeException(ex);
    }
  }


  @Override
  public void setURL (URL v) {
    setURL(++index, v);
  }


  //@Override
  private void setObject (int i, Object obj) {
    try {
      if (obj instanceof Decimal) {
        setDecimal(i, (Decimal)obj);
      } else if (obj instanceof LocalDate) {
        LocalDate d = (LocalDate)obj;
        setDate(i, new java.sql.Date(d.toEpochDay()));
      } else if (obj instanceof Enum<?>) {
        setEnum(i, (Enum<?>)obj);
      } else {
        stmt.setObject(i, obj);
      }
    } catch (SQLException ex) {
      throw new RuntimeException(ex);
    }
  }


  @Override
  public void setObject (Object obj) {
    setObject (++index, obj);
  }


  @Override
  public IResultSet executeQuery(Object... parameters) {
    try {
      int i = 0;
      for (Object p : parameters) {
        setObject(++i, p);
      }
      java.sql.ResultSet rs = stmt.executeQuery();
      return new ResultSet(rs);
    } catch (SQLException ex) {
      throw new SQLExecuteException(stmt, ex);
    }
  }

  
  @Override
  public void setFetchSize (int n) {
    try {
      stmt.setFetchSize(n);
    } catch (SQLException ex) {
      throw new SQLExecuteException(stmt, ex);
    }
  }


//  private static String toSimpleName (String name) {
//    if (name.indexOf('_') >= 0) {
//      boolean toUpper = false;
//      StringBuffer buffer = new StringBuffer();
//      int n = name.length();
//      for (int i = 0; i < n; i++) {
//        char c = name.charAt(i);
//        if (c == '_') {
//          toUpper = true;
//        } else if (toUpper) {
//          buffer.append(Character.toUpperCase(c));
//          toUpper = false;
//        } else {
//          buffer.append(c);
//        }
//      }
//      return buffer.toString();
//    } else {
//      return name;
//    }
//  }
  

//  private void getFields (ResultSet rs) {
//    try {
//      Field[] fx = rowClass.getDeclaredFields();
//      ResultSetMetaData rsmd = rs.getMetaData();
//      int n = rsmd.getColumnCount();
//      fields = new Field[n];
//      mappings = new Mapping[n];
//      for (int i = 0; i < n; i++) {
//        String columnName = rsmd.getColumnName(i);
//        String fieldName = toSimpleName(columnName);
//        for (Field f : fx) {
//          if (f.getName().compareToIgnoreCase(fieldName) == 0) {
//            f.setAccessible(true);
//            fields[i] = f;
//            if (f.getType() == DayDate.class) {
//              mappings[i] = Mapping.DAYDATE;
//            } else if (f.getType() == Decimal.class) {
//              mappings[i] = Mapping.DECIMAL;
//            } else {
//              mappings[i] = Mapping.JDBC; 
//            }
//            break;
//          }
//        }
//        if (fields[i] == null) {
//          throw new RuntimeException(new NoSuchFieldException(fieldName));
//        }
//      }
//    } catch (SecurityException ex) {
//      throw new RuntimeException(ex);
//    } catch (SQLException ex) {
//      throw new RuntimeException(ex);
//    }
//  }

  
//  public T fetchFirst (Object... parameters) {
//    try {
//      int i = 0;
//      for (Object p : parameters) {
//        setObject(i++, p);
//      }
//      ResultSet rs = stmt.executeQuery();
//      if (rs.next()) {
//        // Get fields if not already done
//        if (fields == null) {
//          getFields(rs);
//        }
//        
//        // Create a new row instance and update the values
//        T result = rowClass.newInstance();
//        ResultSetMetaData rsmd = rs.getMetaData();
//        int n = rsmd.getColumnCount();
//        for (i = 0; i < n; i++) {
//          Object value = rs.getObject(i);
//          fields[i].set(result, value);
//        }
//        return result;
//      } else {
//        return null;
//      }
//    } catch (SQLException ex) {
//      throw new RuntimeException(ex);
//    } catch (IllegalAccessException ex) {
//      throw new RuntimeException(ex);
//    } catch (InstantiationException ex) {
//      throw new RuntimeException(ex);
//    }
//  }


  @Override
  public int executeUpdate(Object... values) {
    try {
      int i = 0;
      for (Object v : values) {
        setObject(++i, v);
      }
      return stmt.executeUpdate();
    } catch (SQLException ex) {
      throw new SQLExecuteException(stmt, ex);
    }
  }
  
  
  protected java.sql.ResultSet executeQueryFor (Object[] values) throws SQLException, NoValueException {
    int i = 0;
    for (Object v : values) {
      setObject(++i, v);
    }
    java.sql.ResultSet rs;
    try {
      rs = stmt.executeQuery();
    } catch (SQLException ex) {
      throw new SQLExecuteException(stmt, ex);
    }
    if (rs.next()) {
      return rs;
    } else {
      rs.close();
      throw new NoValueException();
    }
  }
  
  
//  public Object executeQueryForObject (Object... values) {
//    try {
//      int i = 0;
//      for (Object v : values) {
//        setObject(i++, v);
//      }
//      Object result;
//      ResultSet rs = stmt.executeQuery();
//      if (rs.next()) {
//        result = rs.getObject(1);
//      } else {
//        result = null;
//      }
//      rs.close();
//      return result;
//    } catch (SQLException ex) {
//      throw new RuntimeException(ex);
//    }
//  }
  
  
  @Override
  public long getGeneratedKey () {
    try {
      java.sql.ResultSet rs = stmt.getGeneratedKeys();
      long key = 0;
      if (rs != null) {
        if (rs.next()) {
          key = rs.getLong(1);
        }
      }
      return key;
    } catch (SQLException ex) {
      throw new RuntimeException(ex);
    }
  }
  
  
  @Override
  public int executeUpdate () {
    try {
      return stmt.executeUpdate();
    } catch (SQLException ex) {
      throw new RuntimeException(ex);
    }
  }
  
  
  @Override
  public int executeUpdateAllowingException () throws SQLException {
    return stmt.executeUpdate();
  }
  
  
  @Override
  public Timestamp executeQueryForTimestamp (Object... values) {
    try {
      java.sql.ResultSet rs = executeQueryFor(values);
      return rs.getTimestamp(1);
    } catch (SQLException ex) {
      throw new RuntimeException(ex);
    }
  }
  

  @Override
  public int executeQueryForInt (Object... values) {
    try {
      java.sql.ResultSet rs = executeQueryFor(values);
      return rs.getInt(1);
    } catch (SQLException ex) {
      throw new RuntimeException(ex);
    }
  }
  

  @Override
  public String executeQueryForString (Object... values) {
    try {
      java.sql.ResultSet rs = executeQueryFor(values);
      return rs.getString(1);
    } catch (SQLException ex) {
      throw new RuntimeException(ex);
    }
  }
  
  
//  public static void main (String[] args) {
//    EasyDataSource dataSource = new EasyDataSource(new File("db.properties"));
//    EasyConnection conn = dataSource.getConnection();
//    String sql = "select al.id, al.tran_date, al.amount, al.narrative, " +
//                 "       ala.id, " +
//                 "       ala.prime_account_id, al.amount " +
//    		         "  from account_line al, account_line_alloc ala " +
//    		         " where ala.account_line_id = al.id " +
//    		         " order by al.id, ala.id ";
//    EasyStatement stmt = conn.prepareStatement(sql);
//    List<AccountLine> results = stmt.executeQuery(AccountLine.class);
//    for (AccountLine result : results) {
//      result.dump();
//    }
//    stmt.close();
//    conn.close();
//    dataSource.close();
//  }
  
}
