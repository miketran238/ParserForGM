class Test_joda {
	public Object nullSafeGet_old(ResultSet resultSet, String[] names,
            SessionImplementor session, Object owner)
            throws HibernateException, SQLException
    {
        if (resultSet == null)
        {
            return null;
        }
        PersistentDateTime pst = new PersistentDateTime();
        DateTime start = (DateTime) pst.nullSafeGet(resultSet, names[0]);
        DateTime end = (DateTime) pst.nullSafeGet(resultSet, names[1]);
        return new Interval(start, end);
    }
}