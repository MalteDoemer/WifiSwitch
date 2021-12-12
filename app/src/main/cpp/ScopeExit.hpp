//
// Created by malte on 05.12.21.
//

#ifndef WIFI_SWITCH_SCOPEEXIT_HPP
#define WIFI_SWITCH_SCOPEEXIT_HPP

#include <utility>

namespace detail {

    class ScopeExitHelper {
        template<typename F>
        struct ScopeExitCaller {

            ScopeExitCaller(ScopeExitCaller&& other) : f(std::move(other.f)) {}

            ScopeExitCaller(const ScopeExitCaller&) = delete;
            ScopeExitCaller& operator=(const ScopeExitCaller&) = delete;

            ScopeExitCaller(F&& f) : f(std::forward<F>(f)) {}

            F f;
            ~ScopeExitCaller() { f(); }
        };

        template<typename F>
        friend ScopeExitCaller<F> operator+(ScopeExitHelper, F&& f)
        {
            return ScopeExitCaller<F>(std::forward<F>(f));
        }
    };

#define CONCAT_IMPL(s1, s2) s1##s2
#define CONCAT(s1, s2)      CONCAT_IMPL(s1, s2)

#define ANON_VAR(str) CONCAT(str, __COUNTER__)

#define SCOPE_EXIT auto ANON_VAR(SCOPE_EXIT_) = ::detail::ScopeExitHelper() + [&]

}

#endif //WIFI_SWITCH_SCOPEEXIT_HPP
