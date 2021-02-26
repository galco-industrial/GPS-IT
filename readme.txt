
#For Lambda Database connections
==========================================[ Server.xml ]================================================
  <GlobalNamingResources>

    <Resource name="UserDatabase" auth="Container"
              type="org.apache.catalina.UserDatabase"
              description="User database that can be updated and saved"
              factory="org.apache.catalina.users.MemoryUserDatabaseFactory"
              pathname="conf/tomcat-users.xml" />

     <Resource auth="Container" name="jdbc/connectWDS"
              type="javax.sql.DataSource" removeAbandoned="true"
              removeAbandonedTimeout="120"
			  url="jdbc:datadirect:openedge://lambda-app1.galco.com:2011;databaseName=wds-galco"
              username="" password=""
              driverClassName="com.ddtek.jdbc.openedge.OpenEdgeDriver"
              maxActive="10" maxIdle="10" minIdle="0" maxWait="15"
               />
    <Resource auth="Container" name="jdbc/connectSRO"
              type="javax.sql.DataSource" removeAbandoned="true"
              removeAbandonedTimeout="120"
              url="jdbc:datadirect:openedge://lambda-app1.galco.com:2012;databaseName=sro-galco"
              username="root" password=""
              driverClassName="com.ddtek.jdbc.openedge.OpenEdgeDriver"
              maxActive="10" maxIdle="10" minIdle="0" maxWait="15"
               />
    <Resource auth="Container" name="jdbc/connectWWW"
              type="javax.sql.DataSource" removeAbandoned="true"
              removeAbandonedTimeout="120"
              url="jdbc:datadirect:openedge://lambda-app1.galco.com:2017;databaseName=web-galco"
              username="" password=""
              driverClassName="com.ddtek.jdbc.openedge.OpenEdgeDriver"
              maxActive="10" maxIdle="10" minIdle="0" maxWait="15"
               />

  </GlobalNamingResources>




  ==========================================[ Context.xml ]================================================

	<ResourceLink name="jdbc/connectWDS" global="jdbc/connectWDS" type="javax.sql.DataSource"/>
    <ResourceLink name="jdbc/connectSRO" global="jdbc/connectSRO" type="javax.sql.DataSource"/>
    <ResourceLink name="jdbc/connectWWW" global="jdbc/connectWWW" type="javax.sql.DataSource"/>


  ========================================================================================================
