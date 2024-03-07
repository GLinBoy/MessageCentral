import { defineComponent } from 'vue';
import { useI18n } from 'vue-i18n';

export default defineComponent({
  compatConfig: { MODE: 3 },
  name: 'JhiFooter',
  setup() {
    const jhipsterLinks = [
      {
        href: 'https://www.jhipster.tech/',
        target: '_blank',
        rel: 'noopener noreferrer',
        vtext: 'home.link.homepage',
        text: 'JHipster homepage',
      },
      {
        href: 'http://stackoverflow.com/tags/jhipster/info',
        target: '_blank',
        rel: 'noopener noreferrer',
        vtext: 'home.link.stackoverflow',
        text: 'JHipster on Stack Overflow',
      },
      {
        href: 'https://github.com/jhipster/generator-jhipster/issues?state=open',
        target: '_blank',
        rel: 'noopener noreferrer',
        vtext: 'home.link.bugtracker',
        text: 'JHipster bug tracker',
      },
      {
        href: 'https://gitter.im/jhipster/generator-jhipster',
        target: '_blank',
        rel: 'noopener noreferrer',
        vtext: 'home.link.chat',
        text: 'JHipster public chat room',
      },
      {
        href: 'https://twitter.com/jhipster',
        target: '_blank',
        rel: 'noopener noreferrer',
        vtext: 'home.link.follow',
        text: 'follow @jhipster on Twitter',
      },
    ];

    const mcLinks = [
      {
        href: 'https://github.com/GLinBoy/MessageCentral',
        target: '_blank',
        rel: 'noopener noreferrer',
        vtext: 'home.link.homepage',
        text: 'MessageCentral homepage',
      },
      {
        href: 'https://github.com/GLinBoy/MessageCentral/issues?state=open',
        target: '_blank',
        rel: 'noopener noreferrer',
        vtext: 'home.link.bugtracker',
        text: 'MessageCentral bug tracker',
      },
      {
        href: 'https://t.me/MessageCentral',
        target: '_blank',
        rel: 'noopener noreferrer',
        vtext: 'home.link.chat',
        text: 'MessageCentral public chat room',
      },
    ];

    return {
      t$: useI18n().t,
      jhipsterLinks,
      mcLinks,
    };
  },
});
