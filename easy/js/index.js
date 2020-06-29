$(function() {
    //----------------------------------
    var editIndex = undefined;
    /*
     *初始化表格
     *dataGrid能够接受的key只有下面两个
     *total：数据总数
     *rows：返回的数据
     */
    $('#grid').datagrid({
        url: requestIP + 'project/listProject',
        method: 'post',
        queryParams: { // 默认向后端传递page 和 row两个参数
            unitId: $('#searchUnitId').val(),
            buildId: $('#searchBuildId').val(),
            projectDate: $('#searchProjectDate').val(),
            projectId: $('#searchProjectId').val(),
            projectName: $('#searchProjectName').val()
        },
        checkOnSelect: true,
        singleSelect: false, //只能单选行
        rownumbers: true, // 显示行号列
        pagination: true, // 显示分页工具栏
        showFooter: true,
        pageNumber: 1, // 初始化的页数
        pageSize: 10, // 每页显示记录数
        pageList: [3, 5, 10], // 分页工具条中下拉列表中的值
        striped: true, // 隔行换色
        loadMsg: '请稍候，数据加载中...', // 自定义等待消息
        emptyMsg: '查询数据为空...',
        columns: [
            [{
                    field: 'projectId',
                    width: '20%',
                    title: '项目编码',
                    align: 'center'
                },
                {
                    field: 'projectName',
                    width: '20%',
                    title: '项目名称',
                    align: 'center'
                },
                {
                    field: 'unitName',
                    width: '20%',
                    title: '单位',
                    align: 'center'
                },
                {
                    field: 'buildName',
                    width: '20%',
                    title: '建设性质',
                    align: 'center'
                },
                {
                    field: 'planValue',
                    width: '20%',
                    title: '计划值',
                    align: 'center',
                    editor:{
                        type:'numberbox',
                        options:{
                            precision:2,
                            min:0,
                            max:100
                        }
                    }
                },
                {
                    field: 'projectDate',
                    width: '0%',
                    title: '年份',
                    align: 'center',
                    // formatter: function(value) {
                    //     var year = value.substr(0, 4);
                    //     return year;
                    // },
                    hidden:'true'
                }, 
                {field:'unitId', title: '',width:'0%',hidden:'true'},
                {field:'buildId', title: '',width:'0%',hidden:'true'}
            ],
            [{ field: 'projectId', title: '汇总', width: '20%', align: 'center' },
                { field: 'projectName', title: '', width: '20%', align: 'center' },
                { field: 'unitName', title: '', width: '20%', align: 'center' },
                { field: 'buildName', title: '', width: '20%', align: 'center' },
                {
                    field: 'planValue',
                    // title: '<input type="text" disabled="disabled" id="planValueTitle" style="font-size:15px;width:90px;text-align:center;background:none;border:none;" value="100";/>',
                    title:'<span id="planValueTitle"><span>',
                    width: '20%',
                    align: 'center',
                },
                {field: 'projectDate', title: '', width: '20%', align: 'center' },
                {field:'unitId', title: '',width:'0%',hidden:'true'},
                {field:'buildId', title: '',width:'0%',hidden:'true'}
            ]
        ],
        onLoadSuccess: function(data){
            // 表格加载结束，汇总行计算数据
            $("#planValueTitle").html(collectPlanValue().toFixed(2));
        },
        onClickRow:function(rowIndex, rowData) {
			row = rowData.projectId;
        },
        //双击开启编辑，双击结束编辑
        onDblClickCell: function (index, field, value) {
            // 如果处于未编辑态，则进入编辑态
            if (editIndex == undefined) {
                if (field == "planValue") { // 判断是否是field为planValue列，如果不是固定某列的话，不需要判断
                    $(this).datagrid('beginEdit', index);
                    var ed = $(this).datagrid('getEditor', {index: index, field: field});
                    $(ed.target).focus();
                }
                console.log("value:"+value)
                editIndex = index;
            } else if (editIndex != undefined) {//如果不相等，说明已经打开编辑器了，需要关闭编辑器
                $(this).datagrid('endEdit', editIndex);
                editIndex = undefined;
            }
        },
        // 关闭编辑器后执行
        onAfterEdit: function (index, row, changes) { // 关闭编辑器后触发
            $("#planValueTitle").html(collectPlanValue().toFixed(2));
            // 将编辑后的计划值和对应的项目编号添加到数组
            setPlanArray(row.projectId,row.planValue);
        }

    });
    // 设置输入框样式
    setStyle();
    //初始化单位--树形下拉框
    $.ajax({
        url: requestIP + '/project/getTreeNodeByES',
        dataType: 'json',
        success: function(data) {
            $('#searchUnitId').combotree({
                valueField: 'id',
                textField: 'text',
                data: data,
                panelHeight: '250'
            });
            $('#addUnit').combotree({
                valueField: 'id',
                textField: 'text',
                data: data,
                panelHeight: '250'
            });
            $('#updateUnitId').combotree({
                valueField: 'id',
                textField: 'text',
                data: data,
                panelHeight: '250'
            });
        }
    });
    //初始化建设性质--下拉框
    $.ajax({
        url: requestIP + '/project/getBuildProperties',
        dataType: 'json',
        success: function(data) {
            $('#searchBuildId').combobox({
                valueField: 'id',
                textField: 'text',
                data: data,
                panelHeight: 'auto'
            });
            $('#addBuildProperties').combobox({
                valueField: 'id',
                textField: 'text',
                data: data,
                panelHeight: 'auto'
            });
            $('#updateBuildId').combobox({
                valueField: 'id',
                textField: 'text',
                data: data,
                panelHeight: 'auto'
            });
        }
    });

    //年份选择框
    laydate.render({
        elem: '#searchProjectDate', //指定元素 
        type: 'year'
    });
    
});
 
// 多条件查询
function toSearch() {
    console.log("搜索进来了");
    var unitId = $('#searchUnitId').val();
    if(unitId ==0){
        unitId=null
    }
    var buildId = $('#searchBuildId').val();
    if(buildId==0){
        buildId=null
    }
    var queryParams = $('#grid').datagrid('options').queryParams;
    queryParams.buildId = buildId;
    queryParams.unitId = unitId;
    queryParams.projectDate = $('#searchProjectDate').val();
    queryParams.projectName = $('#searchProjectName').val();
    queryParams.projectId = $('#searchProjectId').val();
    $("#grid").datagrid("reload");
}

// 计划值汇总
function collectPlanValue() {
    var sum = 0;
    var rows = $("#grid").datagrid("getRows");//获得总行数
    for (var i = 0; i < rows.length; i++) {//循环
        sum += parseFloat(rows[i]['planValue']); //列名要与之对应
    }
    return sum;
}
// 改变计划值加入数组
var planArray = [];
function setPlanArray(projectId,planValue){
    var changeData = {
        projectId:projectId,
        planValue:parseFloat(planValue) 
    }
    planArray.push(changeData);
    console.log(planArray);
}
// 点击保存 将改变的计划值存入数据库中
function save(){
    console.log(JSON.stringify(planArray))
    $.ajax({
        url:requestIP + "project/updatePlanValue",
        type:'post',
        dataType : "json",
        contentType : 'application/json;charset=utf-8',
        data:JSON.stringify(planArray),
        success : function(data) {
            if (data.status == 1) {
                $.messager.alert('温馨提示',"计划值修改失败，请稍后重试！")
            } else {
                $.messager.alert('温馨提示',"计划值修改成功！");
                planArray.length = 0;
                toSearch();
                
            } 
        }
    })
}
// 清除添加表单
function clearForm() {
    $('#winAdd').form('clear');
}
// 打开添加表单窗口
function add1() {
    clearForm();
    $("#winAdd").window("open");
}
// 执行添加操作
function add() {
    var projectName = $("#addProjectName").val();
    var unitId = $("#addUnit").val();
    var buildId = $("#addBuildProperties").val();
    
    if(projectName ==""||projectName==" "){
        $.messager.alert('温馨提示',"项目名称必填!")
       return;
    }else if(!CheckCharacter(projectName)){
        $.messager.alert('温馨提示',"项目名称包含特殊字符!")
        return;
    }
    if(unitId==0){
        $.messager.alert('温馨提示',"所属单位尚未选择，请选择后重试!")
       return;
    }
    if(buildId==0){
        $.messager.alert('温馨提示',"建设性质尚未选择，请选择后重试!")
       return;
    }
    $.ajax({
        url:requestIP + "project/addProjects",
        type:'get',
        dataType : "json",
        contentType : 'application/json;charset=utf-8',
        data:{
            'projectName' : $("#addProjectName").val(),
            'unitId' : $("#addUnit").val(),
            'buildId':$("#addBuildProperties").val()
            //'planValue':$("#addPlanValue").val()
        },
        success : function(data) {
            if (data.status == 1) {
                $.messager.alert('温馨提示','项目名称已存在，请重新输入');
                // alert("项目名称已存在，请重新输入")
            } else if (data.status == 2) {
                $.messager.alert('温馨提示',"项目添加成功")
                $("#winAdd").window("close");
                toSearch();
            } else {
                $.messager.alert('温馨提示',"项目添加失败，请联系管理员解决")
            }    
        }
    })
}

//点击修改按钮 判断选择 赋值给修改界面
function edit() {
    var getCheckedData = $('#grid').datagrid('getChecked');
    if (getCheckedData.length > 0) {
        if (getCheckedData.length > 1) {
            $.messager.alert('温馨提示',"只能选中一个项目进行修改");
            return;
        }
        for (var i = 0; i < getCheckedData.length; i++) {
            data = getCheckedData[i];
            console.log(data)
            $("#updateProjectId").textbox({ "value": data.projectId });
            $("#updateProjectName").textbox({ "value": data.projectName });

            $('#updateUnitId').combotree({
                url: requestIP + '/project/getTreeNode',
                valueField: 'id',
                textField: 'text',
                data: data,
                panelHeight: '250',
                value: data.unitId
            });
            $('#updateBuildId').combobox({
                url: requestIP + '/project/getBuildProperties',
                valueField: 'id',
                textField: 'text',
                data: data,
                panelHeight: 'auto',
                value: data.buildId
            });

            $("#updatePlanValue").textbox({ "value": data.planValue });
        }
    } else {
        $.messager.alert('温馨提示',"请选中一个项目，再进行修改");
        return;
    }
    $("#winUpdate").window("open");
}

// 项目名称校验特殊字符 存在特殊字符 返回false；不存在返回true
function CheckCharacter(val){
    var regEn = /[`~!@#$%^&*()_+<>?:"{},.\/;'[\]]/im,
	regCn = /[·！#￥（——）：；“”‘、，|《。》？、【】[\]]/im;

    if(regEn.test(val) || regCn.test(val)) {
        return false;
    }
    return true;
}

// 修改项目
function update() {
    var projectName = $("#updateProjectName").val();
    var unitId = $("#updateUnitId").val();
    var buildId = $("#updateBuildId").val();
    
    if(projectName ==""||projectName==" "){
        $.messager.alert('温馨提示',"项目名称必填!")
       return;
    }else if(!CheckCharacter(projectName)){
        $.messager.alert('温馨提示',"项目名称包含特殊字符!")
        return;
    }
    if(unitId==0){
        $.messager.alert('温馨提示',"所属单位不能默认全部，请选择后重试!")
       return;
    }
    if(buildId==0){
        $.messager.alert('温馨提示',"建设性质不能默认全部，请选择后重试!")
       return;
    }

    $.ajax({
        url:requestIP + "project/updateProjects",
        type:'get',
        dataType : "json",
        contentType : 'application/json;charset=utf-8',
        data:{
            'projectId' : $("#updateProjectId").val(),
            'projectName' : $("#updateProjectName").val(),
            'unitId':$("#updateUnitId").val(),
            'buildId':$("#updateBuildId").val()
        },
        success : function(data) {
            if (data.status == 1) {
                $.messager.alert('温馨提示',"项目名称已存在，请重新输入")
            } else if (data.status == 2) {
                $.messager.alert('温馨提示',"项目修改成功");
                // 关闭窗口:
                $("#winUpdate").window("close");
                toSearch();
            } else {
                $.messager.alert('温馨提示',"项目修改失败，请联系管理员解决")
            }  
        }
    })
}

// 删除数据
function del(){
    var getCheckedData = $('#grid').datagrid('getChecked');
    var ids = [];
    if (getCheckedData.length > 0) {
        // 将选中的数据编号放入数组中
        for (var i = 0; i < getCheckedData.length; i++) {
            data = getCheckedData[i];
            ids.push(data.projectId);
        }
        $.messager.confirm('温馨提示','您确认要删除选中的数据吗?',function(r){
            if (r){
                $.ajax({
                    url:requestIP + "project/deleteProjects",
                    type:'post',
                    dataType : "json",
                    traditional : true,
                    data:{
                        ids:ids
                    },
                    success : function(data) {
                        if (data.status == 1) {
                            $.messager.alert('温馨提示',"项目删除失败，请稍后重试！")
                        } else {
                            $.messager.alert('温馨提示',"项目删除成功！");
                            toSearch();
                        } 
                    }
                })
            }
        });
        console.log(ids)
    }else{
        $.messager.alert('温馨提示',"您尚未选中项目，请选择后重试！")
    }
}

function setStyle(){
    //项目编码
    $('#searchProjectId').textbox({
        iconAlign: 'left'
    });
    //项目名称
    $('#searchProjectName').textbox({
        iconAlign: 'left'
    });
    //添加项目名称
    $('#addProjectName').textbox({
        iconAlign: 'left'
    });
    //添加计划值
    $('#addPlanValue').textbox({
        iconAlign: 'left'
    });
    //修改项目名称
    $('#updateProjectName').textbox({
        iconAlign: 'left'
    });
    //添加计划值
    $('#updatePlanValue').textbox({
        iconAlign: 'left'
    });
    //添加项目名称
    $('#updateProjectId').textbox({
        iconAlign: 'left'
    });
}
