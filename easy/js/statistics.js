$(function(){
    toSearch();
    //年份选择框
    laydate.render({
        elem: '#searchTime', //指定元素 
        type: 'year'
    });
    var dataList1 = [
        {
            name:"吉林",
            value:'10'
        },
        {
            name:"辽宁",
            value:'8'
        },
        {
            name:"河北",
            value:'9'
        },
        {
            name:"山西",
            value:'5'
        },
        {
            name:"山东",
            value:'6'
        }
    ]
    var myChart = echarts.init(document.getElementById('pie'));
    var option = {
                title: {
                    text: '各单位投资情况',
                    subtext: '',
                    x: 'left'
                },
                tooltip: {
                    trigger: 'item',
                    formatter: "{a} <br/>{b}({d}%)"
                },
                legend: {
                    orient: 'vertical',
                    left: '30%',  //图例距离左的距离
                    y: 'bottom',  //图例上下居中
                    data: [{
                        name:"吉林",
                        icon:"circle"},
                        {
                            name:"辽宁",
                            icon:"circle"
                        },
                        {
                            name:"河北",
                            icon:"circle"
                        },
                        {
                            name:"山西",
                            icon:"circle"
                        },
                        {
                            name:"山东",
                            icon:"circle"
                        }
                    ],
                    formatter:function(name){
                        let target;
                        for(let i=0;i<dataList1.length;i++){
                            if(dataList1[i].name===name){
                                target=dataList1[i].value
                            }
                        }
                        let arr = [name+"                         "+target]
                        return arr.join("\n")
                    },
                    textStyle:{
                        fontSize:14
                    }
                },
                color: ['rgb(203,155,255)', 'rgb(149,162,255)', 'rgb(58,186,255)',
                        'rgb(119,168,249)', 'rgb(235,161,159)'],//五个数据，五个颜色
                series: [
                {
                    name: '',
                    type: 'pie',
                    radius: ['50%','65%'],  //图的大小
                    center: ['50%', '40%'], //图的位置，距离左跟上的位置
                    emphasis: {
                        label: {
                            show: true,
                            fontSize: '30',
                            fontWeight: 'bold'
                        }
                    },
                    labelLine: {
                        show: false
                    },
                    data:dataList1
                }
                ]
            };

    myChart.setOption(option);

    
    var dataList=[
        {name:"南海诸岛",value:0},
        {name: '北京', value: randomValue()},
        {name: '天津', value: randomValue()},
        {name: '上海', value: randomValue()},
        {name: '重庆', value: randomValue()},
        {name: '河北', value: randomValue()},
        {name: '河南', value: randomValue()},
        {name: '云南', value: randomValue()},
        {name: '辽宁', value: randomValue()},
        {name: '黑龙江', value: randomValue()},
        {name: '湖南', value: randomValue()},
        {name: '安徽', value: randomValue()},
        {name: '山东', value: randomValue()},
        {name: '新疆', value: randomValue()},
        {name: '江苏', value: randomValue()},
        {name: '浙江', value: randomValue()},
        {name: '江西', value: randomValue()},
        {name: '湖北', value: randomValue()},
        {name: '广西', value: randomValue()},
        {name: '甘肃', value: randomValue()},
        {name: '山西', value: randomValue()},
        {name: '内蒙古', value: randomValue()},
        {name: '陕西', value: randomValue()},
        {name: '吉林', value: randomValue()},
        {name: '福建', value: randomValue()},
        {name: '贵州', value: randomValue()},
        {name: '广东', value: randomValue()},
        {name: '青海', value: randomValue()},
        {name: '西藏', value: randomValue()},
        {name: '四川', value: randomValue()},
        {name: '宁夏', value: randomValue()},
        {name: '海南', value: randomValue()},
        {name: '台湾', value: randomValue()},
        {name: '香港', value: randomValue()},
        {name: '澳门', value: randomValue()}
    ]
    var myChart2 = echarts.init(document.getElementById('map'));
    var mapOption = {
        title: {
            text: '各单位投资情况',
            subtext: '',
            x: 'left'
        },
        tooltip: {
                formatter:function(params,ticket, callback){
                    return params.seriesName+'<br />'+params.name+'：'+params.value
                }
            },
        visualMap: {
            min: 0,
            max: 1500,
            left: 'left',
            top: 'bottom',
            text: ['高','低'],
            inRange: {
                color: ['#e0ffff', '#006edd']
            },
            show:true
        },
        geo: {
            map: 'china',
            roam: false,
            zoom:1.23,
            label: {
                normal: {
                    show: true,
                    fontSize:'10',
                    color: 'rgba(0,0,0,0.7)'
                }
            },
            itemStyle: {
                normal:{
                    borderColor: 'rgba(0, 0, 0, 0.2)'
                },
                emphasis:{
                    areaColor: '#F3B329',
                    shadowOffsetX: 0,
                    shadowOffsetY: 0,
                    shadowBlur: 20,
                    borderWidth: 0,
                    shadowColor: 'rgba(0, 0, 0, 0.5)'
                }
            }
        },
        series : [
            {
                name: '信息量',
                type: 'map',
                geoIndex: 0,
                data:dataList
            }
        ]
    };
    myChart2.setOption(mapOption);

})
function randomValue() {
    return Math.round(Math.random()*1000);
}

function toSearch(){
    $.ajax({
        url:requestIP + "project/getStatisticsData",
        type:'get',
        dataType : "json",
        contentType : 'application/json;charset=utf-8',
        data:{
            projectDate: $("#searchTime").val()},
        success : function(data) {
            $.messager.alert('温馨提示',"计划值修改失败，请稍后重试！")
           
        }
    })
}