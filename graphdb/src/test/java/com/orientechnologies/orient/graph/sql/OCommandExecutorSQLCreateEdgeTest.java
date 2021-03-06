package com.orientechnologies.orient.graph.sql;

import java.util.List;

import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;

import com.orientechnologies.orient.core.Orient;
import com.orientechnologies.orient.core.db.document.ODatabaseDocumentTx;
import com.orientechnologies.orient.core.metadata.schema.OSchema;
import com.orientechnologies.orient.core.record.impl.ODocument;
import com.orientechnologies.orient.core.sql.OCommandSQL;
import com.orientechnologies.orient.core.sql.query.OSQLSynchQuery;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

/**
 * @author <a href="mailto:enisher@gmail.com">Artem Orobets</a>
 */
@RunWith(JUnit4.class)
public class OCommandExecutorSQLCreateEdgeTest {

  private static ODatabaseDocumentTx db;
  private static ODocument           owner1;
  private static ODocument           owner2;

  @BeforeClass
  public static void setUp() throws Exception {
    db = Orient.instance().getDatabaseFactory().createDatabase("graph", "memory:target/testdb");

    if (db.exists()) {
      db.open("admin", "admin");
      db.drop();
    }

    db.create();

    final OSchema schema = db.getMetadata().getSchema();
    schema.createClass("Owner", schema.getClass("V"));
    schema.createClass("link", schema.getClass("E"));

    owner1 = new ODocument("Owner");
    owner1.save();
    owner2 = new ODocument("Owner");
    owner2.save();
  }

  @AfterClass
  public static void tearDown() throws Exception {
    db.drop();

    db = null;
    owner1 = null;
    owner2 = null;
  }

  @Test
  public void testParametersBinding() throws Exception {
    db.command(new OCommandSQL("CREATE EDGE link from " + owner1.getIdentity() + " TO " + owner2.getIdentity() + " SET foo = ?"))
        .execute("123");

    final List<ODocument> list = db.query(new OSQLSynchQuery<Object>("SELECT FROM link"));

    Assert.assertEquals(list.size(), 1);
    Assert.assertEquals(list.get(0).field("foo"), "123");
  }
}
