package com.sharepast.dal.dao.hibernate;

import com.sharepast.dal.dao.FileDAO;
import com.sharepast.dal.domain.FileDO;
import com.sharepast.persistence.orm.hibernate.HibernateDao;
import org.springframework.stereotype.Repository;

@Repository("fileDao")
public class FileDAOImpl extends HibernateDao<FileDO> implements FileDAO {

  public FileDAOImpl() {}

  public Class<FileDO> getManagedClass () {
    return FileDO.class;
  }

}
