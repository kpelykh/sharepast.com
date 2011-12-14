package kp.app.dal.dao.hibernate;

import kp.app.dal.dao.FileDAO;
import kp.app.dal.domain.FileDO;
import kp.app.persistence.orm.hibernate.HibernateDao;
import org.springframework.stereotype.Repository;

@Repository("fileDao")
public class FileDAOImpl extends HibernateDao<FileDO> implements FileDAO {

  public FileDAOImpl() {}

  public Class<FileDO> getManagedClass () {
    return FileDO.class;
  }

}
