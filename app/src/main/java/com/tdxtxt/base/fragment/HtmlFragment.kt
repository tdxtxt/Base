package com.tdxtxt.base.fragment

import android.view.View
import com.tdxtxt.base.R
import com.tdxtxt.base.databinding.FragmentHtmlBinding
import com.tdxtxt.baselib.ui.BaseFragment
import com.tdxtxt.baselib.ui.viewbinding.IViewBinding
import com.tdxtxt.baselib.view.html.HHtml

/**
 * <pre>
 *     author : tangdexiang
 *     time   : 2024-03-18
 *     desc   :
 * </pre>
 */
class HtmlFragment : BaseFragment(), IViewBinding<FragmentHtmlBinding> {
    override fun getLayoutId() = R.layout.fragment_html
    override fun view2Binding(rootView: View) = FragmentHtmlBinding.bind(rootView)
    override fun initUi() {
        val html = """<p><strong>长安汽车新进社招人员入职培训说明：</strong></p><p><strong style=\"background-color: yellow;\">强制培训课程：</strong>安全、保密和廉洁等课程，请于收到线上培训任务3天内完成学习并通过考试，人力资源部将不定期抽查；</p><p><strong style=\"background-color: lime;\">战略文化课程：</strong>企业文化、公司战略和组织架构等课程，人力资源部将于每月20-25号其中一天组织培训（脱产1天）；</p><p><strong style=\"background-color: aqua;\">专业领域课程：</strong><strong>研发、制造、营销和职能</strong>四大领域的岗前通用课程，长安学习中心将于每月25号至月底其中一天组织培训（脱产1天）。</p><p>线下培训将提前发送通知，请按时参训。</p><p><strong>入职培训结果将与试用期考核挂钩：</strong>所有课程学习完成并考试通过，则入职培训通过，作为试用期转正考核依据之一。</p>
            |<a href='https://www.baidu.com'>百度</a>
            |<h2>Hello wold</h2><ul><li>cats</li><li>dogs</li></ul>
            |<p>您希望学习的课程3-欧盟法规解读<font color='#03DAC5'><small>[多选题]</small></font><img style=\"max-width: 100%;\" style=\"max-width: 100%;\" src=\"https://learningprod-1307664769.file.myqcloud.com/cover/2023-06-09/rFhqozcaDJa0pNxuXVaWd1AImpXqxXMy.png\"></p>""".trimMargin()

        HHtml.setHtml(viewbinding().tvContent, html, true)
    }
}