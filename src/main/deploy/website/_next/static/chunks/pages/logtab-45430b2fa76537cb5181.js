(self.webpackChunk_N_E=self.webpackChunk_N_E||[]).push([[12],{5923:function(t,e,n){"use strict";var r=n(4942),o=n(5861),i=n(7757),u=n.n(i);function c(t,e){var n=Object.keys(t);if(Object.getOwnPropertySymbols){var r=Object.getOwnPropertySymbols(t);e&&(r=r.filter((function(e){return Object.getOwnPropertyDescriptor(t,e).enumerable}))),n.push.apply(n,r)}return n}function a(t){for(var e=1;e<arguments.length;e++){var n=null!=arguments[e]?arguments[e]:{};e%2?c(Object(n),!0).forEach((function(e){(0,r.Z)(t,e,n[e])})):Object.getOwnPropertyDescriptors?Object.defineProperties(t,Object.getOwnPropertyDescriptors(n)):c(Object(n)).forEach((function(e){Object.defineProperty(t,e,Object.getOwnPropertyDescriptor(n,e))}))}return t}function s(){return(s=(0,o.Z)(u().mark((function t(e){var n,r,o,i,c,s,f=arguments;return u().wrap((function(t){for(;;)switch(t.prev=t.next){case 0:return n=f.length>1&&void 0!==f[1]?f[1]:{},r=n.timeout,o=void 0===r?16e3:r,i=new AbortController,c=setTimeout((function(){return i.abort()}),o),t.next=6,fetch(e,a(a({},n),{},{signal:i.signal}));case 6:return s=t.sent,clearTimeout(c),t.abrupt("return",s);case 9:case"end":return t.stop()}}),t)})))).apply(this,arguments)}e.Z=function(t){return s.apply(this,arguments)}},2167:function(t,e,n){"use strict";var r=n(3038);var o,i=(o=n(7294))&&o.__esModule?o:{default:o},u=n(1063),c=n(4651),a=n(7426);var s={};function f(t,e,n,r){if(t&&u.isLocalURL(e)){t.prefetch(e,n,r).catch((function(t){0}));var o=r&&"undefined"!==typeof r.locale?r.locale:t&&t.locale;s[e+"%"+n+(o?"%"+o:"")]=!0}}},7426:function(t,e,n){"use strict";var r=n(3038);Object.defineProperty(e,"__esModule",{value:!0}),e.useIntersection=function(t){var e=t.rootMargin,n=t.disabled||!u,a=o.useRef(),s=o.useState(!1),f=r(s,2),l=f[0],v=f[1],d=o.useCallback((function(t){a.current&&(a.current(),a.current=void 0),n||l||t&&t.tagName&&(a.current=function(t,e,n){var r=function(t){var e=t.rootMargin||"",n=c.get(e);if(n)return n;var r=new Map,o=new IntersectionObserver((function(t){t.forEach((function(t){var e=r.get(t.target),n=t.isIntersecting||t.intersectionRatio>0;e&&n&&e(n)}))}),t);return c.set(e,n={id:e,observer:o,elements:r}),n}(n),o=r.id,i=r.observer,u=r.elements;return u.set(t,e),i.observe(t),function(){u.delete(t),i.unobserve(t),0===u.size&&(i.disconnect(),c.delete(o))}}(t,(function(t){return t&&v(t)}),{rootMargin:e}))}),[n,e,l]);return o.useEffect((function(){if(!u&&!l){var t=i.requestIdleCallback((function(){return v(!0)}));return function(){return i.cancelIdleCallback(t)}}}),[l]),[d,l]};var o=n(7294),i=n(3447),u="undefined"!==typeof IntersectionObserver;var c=new Map},19:function(t,e,n){"use strict";n.r(e),n.d(e,{default:function(){return b}});var r=n(5671),o=n(3144),i=n(7326),u=n(136),c=n(6215),a=n(1120),s=n(4942),f=n(7294),l=(n(1664),n(7304),n(5923),n(5893));function v(t,e){var n="undefined"!==typeof Symbol&&t[Symbol.iterator]||t["@@iterator"];if(!n){if(Array.isArray(t)||(n=function(t,e){if(!t)return;if("string"===typeof t)return d(t,e);var n=Object.prototype.toString.call(t).slice(8,-1);"Object"===n&&t.constructor&&(n=t.constructor.name);if("Map"===n||"Set"===n)return Array.from(t);if("Arguments"===n||/^(?:Ui|I)nt(?:8|16|32)(?:Clamped)?Array$/.test(n))return d(t,e)}(t))||e&&t&&"number"===typeof t.length){n&&(t=n);var r=0,o=function(){};return{s:o,n:function(){return r>=t.length?{done:!0}:{done:!1,value:t[r++]}},e:function(t){throw t},f:o}}throw new TypeError("Invalid attempt to iterate non-iterable instance.\nIn order to be iterable, non-array objects must have a [Symbol.iterator]() method.")}var i,u=!0,c=!1;return{s:function(){n=n.call(t)},n:function(){var t=n.next();return u=t.done,t},e:function(t){c=!0,i=t},f:function(){try{u||null==n.return||n.return()}finally{if(c)throw i}}}}function d(t,e){(null==e||e>t.length)&&(e=t.length);for(var n=0,r=new Array(e);n<e;n++)r[n]=t[n];return r}function h(t){var e=function(){if("undefined"===typeof Reflect||!Reflect.construct)return!1;if(Reflect.construct.sham)return!1;if("function"===typeof Proxy)return!0;try{return Boolean.prototype.valueOf.call(Reflect.construct(Boolean,[],(function(){}))),!0}catch(t){return!1}}();return function(){var n,r=(0,a.Z)(t);if(e){var o=(0,a.Z)(this).constructor;n=Reflect.construct(r,arguments,o)}else n=r.apply(this,arguments);return(0,c.Z)(this,n)}}var p=function(t){(0,u.Z)(n,t);var e=h(n);function n(t){var o;return(0,r.Z)(this,n),o=e.call(this,t),(0,s.Z)((0,i.Z)(o),"getData",(function(){var t;t="localhost"==window.location.hostname?"http://localhost:4000/logs":"http://10.0.8.2:4000/logs",fetch(t).then((function(t){return t.json()})).then((function(t){o.intervalID=setTimeout(o.getData.bind((0,i.Z)(o)),1),o.setState({logs:t.flat(),newInfoLoaded:!0})}),(function(t){"AbortError"===t.name&&o.setState({timedOut:!0}),o.intervalID=setTimeout(o.getData.bind((0,i.Z)(o)),1)}))})),o.intervalID=null,o.state={text:null,newInfoLoaded:!1,logs:[],timedOut:!1},o}return(0,o.Z)(n,[{key:"componentDidMount",value:function(){this.getData()}},{key:"componentWillUnmount",value:function(){clearTimeout(this.intervalID)}},{key:"render",value:function(){var t=this.state,e=t.error,n=t.newInfoLoaded,r=(t.text,t.logs,t.timedOut);if(e)return(0,l.jsxs)("div",{children:["Error: ",e.message]});if(r)return(0,l.jsx)("div",{class:"text-4xl font-bold text-red-600 text-center",children:"Request Timed Out! Make sure you're connected to the robot wifi"});if(!1===n)return(0,l.jsx)("div",{children:"Loading..."});var o,i=[],u=v(this.state.logs);try{for(u.s();!(o=u.n()).done;){var c=o.value;i.push((0,l.jsxs)(f.Fragment,{children:[c,(0,l.jsx)("br",{})]}))}}catch(a){u.e(a)}finally{u.f()}return(0,l.jsx)("div",{class:"bg-gray-900 w-screen h-screen font-mono",children:(0,l.jsx)("div",{style:{width:"80%",height:"80%"},class:"m-2 bg-gray-600 text-white flex overflow-scroll flex-col-reverse",children:i})})}}]),n}(f.Component);function b(){return(0,l.jsx)(p,{})}},3823:function(t,e,n){(window.__NEXT_P=window.__NEXT_P||[]).push(["/logtab",function(){return n(19)}])},1664:function(t,e,n){n(2167)},5861:function(t,e,n){"use strict";function r(t,e,n,r,o,i,u){try{var c=t[i](u),a=c.value}catch(s){return void n(s)}c.done?e(a):Promise.resolve(a).then(r,o)}function o(t){return function(){var e=this,n=arguments;return new Promise((function(o,i){var u=t.apply(e,n);function c(t){r(u,o,i,c,a,"next",t)}function a(t){r(u,o,i,c,a,"throw",t)}c(void 0)}))}}n.d(e,{Z:function(){return o}})}},function(t){t.O(0,[774,888,179],(function(){return e=3823,t(t.s=e);var e}));var e=t.O();_N_E=e}]);