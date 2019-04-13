package com.microservices.service;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.List;

import com.jfinal.kit.StrKit;
import com.jfinal.plugin.activerecord.Model;
import com.jfinal.plugin.activerecord.Page;
import com.microservices.db.model.MicroservicesModel;
import com.microservices.exception.MicroservicesException;
import com.microservices.utils.ArrayUtils;
import com.microservices.utils.ClassKits;

/**
 * MicroservicesServiceBase 类
 */
public class MicroservicesServiceBase<M extends MicroservicesModel<M>> {

	protected M DAO = null;

	public MicroservicesServiceBase() {
		Class<M> modelClass = null;
		Type t = ClassKits.getUsefulClass(getClass()).getGenericSuperclass();
		if (t instanceof ParameterizedType) {
			Type[] p = ((ParameterizedType) t).getActualTypeArguments();
			modelClass = (Class<M>) p[0];
		}

		if (modelClass == null) {
			throw new MicroservicesException("can not get parameterizedType in MicroservicesServiceBase");
		}

		DAO = ClassKits.newInstance(modelClass);
	}

	public M getDao() {
		return DAO;
	}

	/**
	 * 根据ID查找model
	 *
	 * @param id
	 * @return
	 */
	public M findById(Object id) {
		return DAO.findById(id);
	}

	/**
	 * 查找全部数据
	 *
	 * @return
	 */
	public List<M> findAll() {
		return DAO.findAll();
	}

	/**
	 * 根据ID 删除model
	 *
	 * @param id
	 * @return
	 */
	public boolean deleteById(Object id) {
		MicroservicesModel model = findById(id);
		return model == null ? false : model.delete();
	}

	/**
	 * 删除
	 *
	 * @param model
	 * @return
	 */
	public boolean delete(M model) {
		return model.delete();
	}

	/**
	 * 保存到数据库
	 *
	 * @param model
	 * @return
	 */
	public boolean save(M model) {
		return model.save();
	}

	/**
	 * 保存或更新
	 *
	 * @param model
	 * @return
	 */
	public boolean saveOrUpdate(M model) {
		return model.saveOrUpdate();
	}

	/**
	 * 更新
	 *
	 * @param model
	 * @return
	 */
	public boolean update(M model) {
		return model.update();
	}

	public void join(Page<? extends Model> page, String joinOnField) {
		join(page.getList(), joinOnField);
	}

	public void join(Page<? extends Model> page, String joinOnField, String[] attrs) {
		join(page.getList(), joinOnField, attrs);
	}

	public void join(List<? extends Model> models, String joinOnField) {
		if (ArrayUtils.isNotEmpty(models)) {
			for (Model m : models) {
				join(m, joinOnField);
			}
		}
	}

	public void join(List<? extends Model> models, String joinOnField, String[] attrs) {
		if (ArrayUtils.isNotEmpty(models)) {
			for (Model m : models) {
				join(m, joinOnField, attrs);
			}
		}
	}

	public void join(Page<? extends Model> page, String joinOnField, String joinName) {
		join(page.getList(), joinOnField, joinName);
	}

	public void join(List<? extends Model> models, String joinOnField, String joinName) {
		if (ArrayUtils.isNotEmpty(models)) {
			for (Model m : models) {
				join(m, joinOnField, joinName);
			}
		}
	}

	public void join(Page<? extends Model> page, String joinOnField, String joinName, String[] attrs) {
		join(page.getList(), joinOnField, joinName, attrs);
	}

	public void join(List<? extends Model> models, String joinOnField, String joinName, String[] attrs) {
		if (ArrayUtils.isNotEmpty(models)) {
			for (Model m : models) {
				join(m, joinOnField, joinName, attrs);
			}
		}
	}

	/**
	 * 添加关联数据到某个model中去，避免关联查询，提高性能。
	 *
	 * @param model 要添加到的model
	 * @param joinOnField model对于的关联字段
	 */
	public void join(Model model, String joinOnField) {
		if (model == null)
			return;
		Object id = model.get(joinOnField);
		if (id == null) {
			return;
		}
		Model m = findById(id);
		if (m != null) {
			model.put(StrKit.firstCharToLowerCase(m.getClass().getSimpleName()), m);
		}
	}

	/**
	 * 添加关联数据到某个model中去，避免关联查询，提高性能。
	 *
	 * @param model
	 * @param joinOnField
	 * @param attrs
	 */
	public void join(Model model, String joinOnField, String[] attrs) {
		if (model == null)
			return;
		Object id = model.get(joinOnField);
		if (id == null) {
			return;
		}
		MicroservicesModel m = findById(id);
		if (m != null) {
			m = m.copy();
			m.keep(attrs);
			model.put(StrKit.firstCharToLowerCase(m.getClass().getSimpleName()), m);
		}
	}

	/**
	 * 添加关联数据到某个model中去，避免关联查询，提高性能。
	 *
	 * @param model
	 * @param joinOnField
	 * @param joinName
	 */
	public void join(Model model, String joinOnField, String joinName) {
		if (model == null)
			return;
		Object id = model.get(joinOnField);
		if (id == null) {
			return;
		}
		Model m = findById(id);
		if (m != null) {
			model.put(joinName, m);
		}
	}

	/**
	 * 添加关联数据到某个model中去，避免关联查询，提高性能。
	 *
	 * @param model
	 * @param joinOnField
	 * @param joinName
	 * @param attrs
	 */
	public void join(Model model, String joinOnField, String joinName, String[] attrs) {
		if (model == null)
			return;
		Object id = model.get(joinOnField);
		if (id == null) {
			return;
		}
		MicroservicesModel m = findById(id);
		if (m != null) {
			m = m.copy();
			m.keep(attrs);
			model.put(joinName, m);
		}

	}

	public void keep(Model model, String... attrs) {
		if (model == null) {
			return;
		}

		model.keep(attrs);
	}

	public void keep(List<? extends Model> models, String... attrs) {
		if (ArrayUtils.isNotEmpty(models)) {
			for (Model m : models) {
				keep(m, attrs);
			}
		}
	}
}
