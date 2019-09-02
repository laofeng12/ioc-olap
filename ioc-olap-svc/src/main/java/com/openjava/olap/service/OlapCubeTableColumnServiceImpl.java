package com.openjava.olap.service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import javax.annotation.Resource;

import com.openjava.olap.domain.OlapCubeTable;
import com.openjava.olap.domain.OlapCubeTableColumn;
import com.openjava.olap.mapper.kylin.*;
import com.openjava.olap.query.OlapCubeTableColumnDBParam;
import com.openjava.olap.repository.OlapCubeTableColumnRepository;
import com.openjava.olap.service.OlapCubeTableColumnService;
import org.apache.commons.lang3.StringUtils;
import org.ljdp.component.sequence.ConcurrentSequence;
import org.ljdp.component.sequence.SequenceService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * 文件夹表业务层
 *
 * @author xiepc
 */
@Service
@Transactional
public class OlapCubeTableColumnServiceImpl implements OlapCubeTableColumnService {

	@Resource
	private OlapCubeTableColumnRepository olapCubeTableColumnRepository;

	public Page<OlapCubeTableColumn> query(OlapCubeTableColumnDBParam params, Pageable pageable) {
		Page<OlapCubeTableColumn> pageresult = olapCubeTableColumnRepository.query(params, pageable);
		return pageresult;
	}

	public List<OlapCubeTableColumn> queryDataOnly(OlapCubeTableColumnDBParam params, Pageable pageable) {
		return olapCubeTableColumnRepository.queryDataOnly(params, pageable);
	}

	public OlapCubeTableColumn get(Long id) {
		Optional<OlapCubeTableColumn> o = olapCubeTableColumnRepository.findById(id);
		if (o.isPresent()) {
			OlapCubeTableColumn m = o.get();
			return m;
		}
		System.out.println("找不到记录OlapCubeTableColumn：" + id);
		return null;
	}

	public ArrayList<OlapCubeTableColumn> findByColumn(String cubeName) {
		return olapCubeTableColumnRepository.findByColumn(cubeName);
	}

	public ArrayList<OlapCubeTableColumn> findByCubeTableId(Long tableId) {
		return olapCubeTableColumnRepository.findByCubeTableId(tableId);
	}

	public OlapCubeTableColumn doSave(OlapCubeTableColumn m) {
		return olapCubeTableColumnRepository.save(m);
	}

	public void doDelete(Long id) {
		olapCubeTableColumnRepository.deleteById(id);
	}

	public void doRemove(String ids) {
		String[] items = ids.split(",");
		for (int i = 0; i < items.length; i++) {
			olapCubeTableColumnRepository.deleteById(new Long(items[i]));
		}
	}

	public void deleteCubeId(Long cubeId) {
		olapCubeTableColumnRepository.deleteCubeId(cubeId);
	}


	@Override
	public ArrayList<OlapCubeTableColumn> getListByTableId(Long cubeTableId) {
		return olapCubeTableColumnRepository.findByTableId(cubeTableId);
	}

	//保存OLAP_CUBE_TABLE_COLUMN表
	public void saveCubeTableColumn(CubeDescMapper cube, ModelsDescDataMapper modelDescData, Long
			cubeId, List<OlapCubeTable> dmEntity) {
		CubeDescDataMapper cubeDescData = cube.getCubeDescData();
		ArrayList<String> column = new ArrayList<>();
		ArrayList<LookupsMapper> lookups = modelDescData.getLookups();
		SequenceService ss = ConcurrentSequence.getInstance();
		ArrayList<MeasureMapper> measuresList = cubeDescData.getMeasures();


		//根据是否存在立方体ID去判断是否为修改, 如果是为修改则根据olap_cube的Id删除column里的数据
		if (StringUtils.isNotBlank(cubeDescData.getUuid())) {
			deleteCubeId(cubeId);
		}
		//Cube里dimensions的处理
		for (DimensionMapper mm : cubeDescData.getDimensions()) {
			//dimensions需要用别名去验证改列属于哪个表
			Optional<OlapCubeTable> dmCube = dmEntity.stream()
					.filter(p -> p.getTableAlias().equals(mm.getTable())).findFirst();
			if (dmCube.isPresent()) {
				OlapCubeTableColumn CubeTableColumn = new OlapCubeTableColumn();
				CubeTableColumn.setCubeTableColumnId(ss.getSequence());
				CubeTableColumn.setName(mm.getName());//列中文名称
				CubeTableColumn.setCubeId(cubeId);//立方体ID
				CubeTableColumn.setTableId(dmCube.get().getId());//表ID
				if (StringUtils.isNotBlank(mm.getColumn())) {
					CubeTableColumn.setColumnName(mm.getColumn());//列名称
				} else {
					String str = StringUtils.join(mm.getDerived(), ",");
					CubeTableColumn.setColumnName(str);//列名称
				}
				CubeTableColumn.setColumnType(mm.getColumn_type());//列类型 HIVE基本数据类型
				CubeTableColumn.setLibraryTable(mm.getId());//前端需要的-库名表名
				CubeTableColumn.setColumnAlias(mm.getName());//列别名
				CubeTableColumn.setIsNew(true);
				CubeTableColumn.setExpressionFull("{0}.{1} as {2}");//完整表达式
				doSave(CubeTableColumn);

				column.add(mm.getTable() + "." + CubeTableColumn.getColumnName());
			}
		}


		//Cube里measures的处理
		for (MeasureMapper mm : measuresList) {
			SequenceService columnService = ConcurrentSequence.getInstance();

			if (mm.function.getExpression().equals("COUNT")) {
				continue;
			}

			//measures需要用列名前面的表别名去cubetabla里拿到数据
			String tableNameColumn = mm.function.parameter.getValue();
			if (!mm.function.getExpression().equals("COUNT")) {
				tableNameColumn = tableNameColumn.substring(0, tableNameColumn.indexOf("."));
			}
			String tableColumn = tableNameColumn;
			Optional<OlapCubeTable> dmCube = dmEntity.stream()
					.filter(p -> p.getTableAlias().equals(tableColumn)).findFirst();

			if (dmCube.isPresent()) {
				OlapCubeTableColumn CubeTableColumn = new OlapCubeTableColumn();
				CubeTableColumn.setCubeTableColumnId(columnService.getSequence());
				CubeTableColumn.setCubeId(cubeId);//立方体ID
				CubeTableColumn.setTableId(dmCube.get().getId());//表ID

				String columnTableName = mm.function.parameter.getValue().substring(mm.function.parameter.getValue().indexOf(".") + 1);
				CubeTableColumn.setColumnAlias(columnTableName);//列别名
				CubeTableColumn.setName(columnTableName);//列中文名称
				CubeTableColumn.setColumnName(columnTableName);//真实列名称

				String primaryType = mm.function.getExpression() == "AVG" ? null : mm.function.getExpression();
				String columnType = mm.function.getExpression();

				String expression = columnType + "({0}.{1}) as {2}";
				CubeTableColumn.setColumnType(mm.function.getReturntype());//列类型 HIVE基本数据类型
				CubeTableColumn.setIsNew(true);
				CubeTableColumn.setPrimaryType(primaryType);//原类型为(AVG会转换成SUM,所以需要定义一个原类型,方便编辑的时候用到)
				CubeTableColumn.setExpressionType(columnType);//表达式类型max、min、sum
				CubeTableColumn.setExpressionFull(expression);//完整表达式
				doSave(CubeTableColumn);

				column.add(tableColumn + "." + CubeTableColumn.getColumnName());
			}
		}

		//models里lookups的处理
		for (LookupsMapper lk : lookups) {

			for (int i = 0; i < lk.join.getForeign_key().length; i++) {
				String join = lk.join.getForeign_key()[i];
				String columnType = lk.join.getFk_type().get(i);
				saveColumn(column, dmEntity, join, columnType, cubeId);

				String joinPk = lk.join.getPrimary_key()[i];
				String columnTypePk = lk.join.getPk_type().get(i);
				saveColumn(column, dmEntity, joinPk, columnTypePk, cubeId);
			}
//            for (int i = 0; i < lk.join.getPrimary_key().length; i++) {
//                String join = lk.join.getPrimary_key()[i];
//                String columnType = lk.join.getPk_type().get(i);
//                saveColumn(column, dmEntity, join, columnType, cubeId);
//            }
		}
	}

	public void saveColumn(ArrayList<String> column, List<OlapCubeTable> dmEntity, String join, String
			columnType, Long cubeId) {
		SequenceService ss = ConcurrentSequence.getInstance();
		String tableName = join.substring(0, join.indexOf("."));
		String columnName = join.substring(join.indexOf(".") + 1);

		boolean isColumn = column.contains(join);

		Optional<OlapCubeTable> dmCube = dmEntity.stream()
				.filter(p -> p.getTableAlias().equals(tableName)).findFirst();
		//判断是否有这个表
		if (isColumn == false) {
			OlapCubeTableColumn CubeTableColumn = new OlapCubeTableColumn();
			CubeTableColumn.setCubeTableColumnId(ss.getSequence());
			CubeTableColumn.setCubeId(cubeId);//立方体ID

			CubeTableColumn.setColumnType(columnType);//列类型 HIVE基本数据类型
			CubeTableColumn.setTableId(dmCube.get().getId());//表ID
			CubeTableColumn.setIsNew(true);
			CubeTableColumn.setExpressionFull("{0}.{1} as {2}");//完整表达式

			CubeTableColumn.setName(columnName);//列中文名称
			CubeTableColumn.setColumnName(columnName);//列名称
			CubeTableColumn.setColumnAlias(columnName);//列别名
			doSave(CubeTableColumn);
		}
	}
}
