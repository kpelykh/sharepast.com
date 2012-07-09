package com.sharepast.dao;

import com.sharepast.domain.FileDO;
import com.sharepast.genericdao.hibernate.GenericDAOImpl;
import org.springframework.stereotype.Repository;

@Repository("fileDao")
public class FileDAOImpl extends GenericDAOImpl<FileDO, Integer> implements FileDAO {


}
