package a.ramvark.ab;
import b.xwriter;
import a.ramvark.itm;
import a.ramvark.ls;
import a.ramvark.lst;
public @ls(cls=expense.class)class expenses extends lst{static final long serialVersionUID=1;
	private int sum;
	protected void rendlisthead(final xwriter x){
		x.nl().style();
		x.css("table.tb","box-shadow:0 0 .5em rgba(0,0,0,1)");
		x.css("table.tb tr:hover,tr:active,tr:focus","background:#fff");
		x.css("table tr:nth-of-type(odd)","background-color:#fff");
		x.css("table.tb tr th","padding:.5em 1em .4em 1em;background:#fff;border:1px solid #ddd");
		x.css("table.tb tr td","padding:.5em 1em .5em 1em;border:1px dotted #ddd");
		x.css("table.tb tr:last-child","border-top:1px solid green;font-weight:bold");
		x.styleEnd();
		x.nl().table("tb");
		x.nl().tr().th().p("name").th().p("price").th().p("amount").th().p("unit");
		x.th();
		sum=0;
	}
	protected void rendrow(final xwriter x,final itm ee)throws Throwable{
		final expense e=(expense)ee;
		x.nl().tr();
		x.td();
		rendldax(x,ee);
		x.td().p(e.price);
		x.td().p(e.amount);
		sum+=e.price.toint()*e.amount.toint();
		x.td().p(e.unit);
		x.td();
		renddelax(x,ee,"x");
	}
	protected void rendlistfoot(final xwriter x){
		x.nl().tr();
		x.td();
		x.td();
		x.td().p(sum);
		x.td();
		x.td();

		x.nl().tableEnd();
	}

}
