(self.webpackChunk_N_E=self.webpackChunk_N_E||[]).push([[377],{7899:function(e,n,t){"use strict";t.r(n),t.d(n,{default:function(){return f}});var i=t(5861),s=t(7757),a=t.n(s),r=t(5725),o=t(2658),u=t(7910),c=t(6808),l=t(9535),d=t(9669),x=t.n(d),h=t(7294),p=(t(7304),t(5893));function f(){var e=(0,h.useState)(0),n=e[0],t=e[1],s=(0,h.useState)(0),d=s[0],f=s[1],j=(0,h.useState)(0),m=j[0],Z=j[1],v=(0,h.useState)(0),P=v[0],g=v[1],C=(0,h.useState)(0),b=C[0],w=C[1],_=(0,h.useState)(0),k=_[0],S=_[1],E="http://10.0.8.2:4000",F=function(){var e=(0,i.Z)(a().mark((function e(){var n;return a().wrap((function(e){for(;;)switch(e.prev=e.next){case 0:return e.next=2,x().get(E+"/shooter/distance");case 2:n=e.sent,w(n.data),S(setTimeout(F,100));case 5:case"end":return e.stop()}}),e)})));return function(){return e.apply(this,arguments)}}();return(0,h.useEffect)((0,i.Z)(a().mark((function e(){return a().wrap((function(e){for(;;)switch(e.prev=e.next){case 0:return e.next=2,F();case 2:return e.abrupt("return",(function(){clearTimeout(k)}));case 3:case"end":return e.stop()}}),e)}))),[]),(0,p.jsxs)("div",{children:[(0,p.jsx)("div",{class:"p-5"}),(0,p.jsxs)(r.ZP,{container:!0,spacing:2,children:[(0,p.jsx)(r.ZP,{item:!0,xs:12,children:(0,p.jsxs)(o.Z,{children:["Estimated Distance: ",b,(0,p.jsx)("br",{}),"Estimated your mom: 1000000000000000"]})}),(0,p.jsx)(r.ZP,{item:!0,xs:1,children:(0,p.jsxs)(o.Z,{children:["Hood: ",n]})}),(0,p.jsx)(r.ZP,{item:!0,xs:9,children:(0,p.jsx)(u.ZP,{min:-12e3,max:24e3,defaultValue:0,value:parseFloat(n)||0,onChange:function(e,n){t(n)}})}),(0,p.jsx)(r.ZP,{item:!0,xs:1,children:(0,p.jsx)(c.Z,{id:"outlined-basic",label:"Value",variant:"outlined",inputProps:{inputMode:"numeric",pattern:"[0-9]*"},onChange:function(e){t(e.target.value)},value:n})}),(0,p.jsx)(r.ZP,{item:!0,xs:1,children:(0,p.jsx)(l.Z,{variant:"outlined",onClick:function(){x().post(E+"/shooter/hood",{angle:parseFloat(n)}).then()},children:"Set "})}),(0,p.jsx)(r.ZP,{item:!0,xs:1,children:(0,p.jsxs)(o.Z,{children:["Lower: ",m]})}),(0,p.jsx)(r.ZP,{item:!0,xs:9,children:(0,p.jsx)(u.ZP,{min:-12e3,max:24e3,defaultValue:0,value:parseFloat(m)||0,onChange:function(e,n){Z(n)}})}),(0,p.jsx)(r.ZP,{item:!0,xs:1,children:(0,p.jsx)(c.Z,{id:"outlined-basic",label:"Value",variant:"outlined",inputProps:{inputMode:"numeric",pattern:"[0-9]*"},onChange:function(e){Z(e.target.value)},value:m})}),(0,p.jsx)(r.ZP,{item:!0,xs:1,children:(0,p.jsx)(l.Z,{variant:"outlined",onClick:function(){x().post(E+"/shooter/lower",{velocity:parseFloat(m)}).then()},children:" Set "})}),(0,p.jsx)(r.ZP,{item:!0,xs:1,children:(0,p.jsxs)(o.Z,{children:["Upper: ",d]})}),(0,p.jsx)(r.ZP,{item:!0,xs:9,children:(0,p.jsx)(u.ZP,{min:-12e3,max:24e3,defaultValue:0,value:parseFloat(d)||0,onChange:function(e,n){f(n)}})}),(0,p.jsx)(r.ZP,{item:!0,xs:1,children:(0,p.jsx)(c.Z,{id:"outlined-basic",label:"Value",variant:"outlined",inputProps:{inputMode:"numeric",pattern:"[0-9]*"},onChange:function(e){f(e.target.value)},value:d})}),(0,p.jsx)(r.ZP,{item:!0,xs:1,children:(0,p.jsx)(l.Z,{variant:"outlined",onClick:function(){x().post(E+"/shooter/upper",{velocity:parseFloat(d)}).then()},children:" Set "})}),(0,p.jsx)(r.ZP,{item:!0,xs:4,children:(0,p.jsx)(l.Z,{variant:"contained",onClick:function(){x().post(E+"/shooter/shoot").then()},children:"Shoot"})}),(0,p.jsx)(r.ZP,{item:!0,xs:4,children:(0,p.jsx)(c.Z,{id:"outlined-basic",label:"Distance in Inches",variant:"outlined",inputProps:{inputMode:"numeric",pattern:"[0-9]*"},onChange:function(e){g(parseInt(e.target.value)||0)},value:P})}),(0,p.jsx)(r.ZP,{item:!0,xs:4,children:(0,p.jsx)(l.Z,{variant:"contained",onClick:function(){g(0),x().post(E+"/shooter/move",{distance:P}).then()},children:"Move"})})]})]})}},3017:function(e,n,t){(window.__NEXT_P=window.__NEXT_P||[]).push(["/shooter",function(){return t(7899)}])}},function(e){e.O(0,[774,183,888,179],(function(){return n=3017,e(e.s=n);var n}));var n=e.O();_N_E=n}]);