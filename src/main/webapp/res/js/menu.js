/**
 *
 * @type {{id: string, name: string, subMenu: {id: string, name: string, url: string}[]}[]}
 * id:生成横向主菜单的ID
 * name:菜单名称
 * subMenu:该菜单对应的子菜单
 *      --id:子菜单对应的ID
 *      --name:子菜单名称
 *      ---url:子菜单点击时加载的URL
 */

var menus = [
    {
        id: "index_menu",
        name: "首页",
        desc: "常用操作",
        subMenu: [
            {
                id: "index_welcome",
                name: "欢迎页",
                url: "/welcome"
            },
            {
                id: "index_about",
                name: "关于我们",
                url: "/about"
            }
        ]
    }
    ,{
        id: "menu_adminlog",
        name: "设置",
        desc: "设置相关",
        subMenu: [
            //   {
            //	id: "adminlog_list",
            //	name: "操作日志",
            //	url: "/adminlog/list"
            //},
            {
                id: "area_list",
                name: "配送地区",
                url: "/setting/area/list"
            },{
                id: "express_list",
                name: "快递公司",
                url: "/setting/express/list"
            },{
                id: "payment_list",
                name: "支付方式",
                url: "/setting/payment/list"
            },{
                id:"doc_api",
                name:"API管理",
                url:"/doc/api/list?typeid=1"
            },{
                id:"doc_entity",
                name:"实体管理",
                url:"/doc/api/docentity/list"
            }]
    },
    {
        id: "goods_menu",
        name: "商品",
        desc: "商品相关",
        subMenu: [
            {
                id: "goods_type_list",
                name: "类型管理",
                url: "/goods/type/list"
            },
            {
                id: "goods_class_list",
                name: "分类管理",
                url: "/goods/class/list"
            },
            {
                id: "brand_list",
                name: "品牌管理",
                url: "/goods/brand/list"
            },
            {
                id: "goods_list",
                name: "商品管理",
                url: "/goods/goodsCommon/list"
            },
            {
                id: "goods_spec_list",
                name: "规格管理",
                url: "/goods/spec/list"
            }
        ]
    },
    {
        id: "store_meun",
        name: "店铺",
        desc: "店铺相关",
        subMenu: [
            {
                id: "store_list",
                name: "店铺管理",
                url: "/store/manager/list"
            },
            {
                id: "store_class",
                name: "店铺分类",
                url: "/store/classs/list"
            },
            {
                id: "store_level",
                name: "店铺等级",
                url: "/store/grade/list"
            },
            /*{
             id: "store_domain",
             name: "二级域名",
             url: "/store/setting/index"
             },*/
            {
                id: "store_tracelog",
                name: "店铺动态",
                url: "/store/tracelog/list"
            }
        ]
    },
    {
        id: "member_meun",
        name: "会员",
        desc: "会员相关",
        subMenu: [
            {
                id: "member_list",
                name: "会员管理",
                url: "/member/list"
            }
        ]
    },
    {
        id: "trade_menu",
        name: "交易",
        desc: "交易相关",
        subMenu: [
            {
                id: "order_list",
                name: "订单管理",
                url: "/orders/list"
            },
            {
                id: "consulting_list",
                name: "咨询管理",
                url: "/trade/consult/list"
            },
            {
                id: "evalGoods_list",
                name: "评价管理",
                url: "/trade/evalGoods/list"
            }
        ]
    },
    {
        id: "website_meun",
        name: "网站",
        desc: "网站相关",
        subMenu: [
            {
                id: "ac_list",
                name: "文章分类",
                url: "/website/class/list"
            },
            {
                id: "article_list",
                name: "文章管理",
                url: "/website/article/index"
            },
            {
                id: "systemArticle_list",
                name: "系统文章",
                url: "/website/document/list"
            },
            {
                id: "pageNav_list",
                name: "页面导航",
                url: "/website/navigation/list"
            },
            {
                id: "website_index",
                name: "首页管理",
                url: "/website/index/list"
            }
        ]
    }
];