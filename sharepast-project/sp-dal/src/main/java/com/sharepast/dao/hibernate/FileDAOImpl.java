package com.sharepast.dao.hibernate;

import com.sharepast.dao.FileDAO;
import com.sharepast.domain.FileDO;
import com.sharepast.domain.GeographicLocationDO;
import com.sharepast.persistence.hibernate.HibernateDao;
import org.springframework.stereotype.Repository;

@Repository("fileDao")
public class FileDAOImpl extends HibernateDao<FileDO> implements FileDAO {

  public FileDAOImpl() {}

    public Class<FileDO> getEntityClass() {
        return FileDO.class;
    }

}
